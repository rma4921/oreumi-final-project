package com.estsoft.finalproject.content.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.estsoft.finalproject.content.model.dto.AlanResponseDto;
import com.estsoft.finalproject.content.model.dto.NewsDetailItem;
import com.estsoft.finalproject.content.model.dto.NewsSummaryItem;
import com.estsoft.finalproject.content.model.dto.ResponseDto;
import com.estsoft.finalproject.content.prompting.SimpleAlanKoreanPromptBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AlanCommunicationService {

    private final RestClient restClient;
    private final String ALAN_URL = "https://kdt-api-function.azurewebsites.net/api/v1/question";
    private final String ALAN_API_KEY;
    org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("AlanCommunicationService");

    public AlanCommunicationService(@Value("${ai.est.alan.client-id:dummy-api-key}") String apiKey,
                                    RestClient restClient) {
        this.restClient = restClient;
        this.ALAN_API_KEY = apiKey;
    }

    public AlanResponseDto getResultFromAlan(String question) {
        String question_encoded = "";
        try {
            question_encoded = URLEncoder.encode(question, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new AlanResponseDto(question,
                "An error occurred while decoding your question: " + e.getMessage(),
                LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return restClient.get().
            uri(URI.create(new StringBuilder(ALAN_URL).append("?content=").append(question_encoded)
                .append("&client_id=").append(ALAN_API_KEY).toString())).
            exchange((req, res) -> {
                StringBuilder resBuilder = new StringBuilder();
                BufferedReader br = Optional.of(res.getBody())
                    .map(body -> new BufferedReader(new InputStreamReader(body))).get();
                while (true) {
                    int i = br.read();
                    if (i < 0) {
                        break;
                    }
                    resBuilder.append((char) i);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> jsonRes = objectMapper.readValue(resBuilder.toString(),
                    new TypeReference<Map<String, Object>>() {
                    });
                String ret = jsonRes.getOrDefault("content", "").toString();
                if (ret.isEmpty()) {
                    return new AlanResponseDto(question,
                        "An error occurred while parsing the response from Alan",
                        LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    return new AlanResponseDto(question, ret, LocalDateTime.now(),
                        res.getStatusCode());
                }
            });
    }

    public AlanResponseDto translateTextKoreanToEnglish(String textToTranslate) {
        String translateQuery = new StringBuilder("다음 문장 영어로 번역해줘: \"").append(textToTranslate)
            .append("\"").toString();
        return getResultFromAlan(translateQuery);
    }

    public AlanResponseDto translateTextEnglishToKorean(String textToTranslate) {
        String translateQuery = new StringBuilder("다음 문장 한국어로 번역해줘: \"").append(textToTranslate)
            .append("\"").toString();
        return getResultFromAlan(translateQuery);
    }

    public ResponseDto<Map<String, String>> findRelatedStock(String articleSummaryOrUrl,
        boolean isURL) {
        LinkedHashMap<String, String> ret = new LinkedHashMap<>();
        StringBuilder promptBuilder = new StringBuilder();
        if (isURL) {
            promptBuilder.append("다음 URL에 있는 기사와 관련된 주식 종목들을 찾아줘 : \"");
        } else {
            promptBuilder.append("다음 글과 관련된 주식 종목들을 찾아줘 : \"");
        }
        promptBuilder.append(articleSummaryOrUrl)
            .append("\"")
            .append(" 답변은 다음 형식으로 해줘: \"")
            .append("[{ \"company\" : (회사명)}]");
        AlanResponseDto companyInfos = getResultFromAlan(promptBuilder.toString());

        if (companyInfos.getResponseCode() != HttpStatus.OK) {
            return ResponseDto.builder(Collections.unmodifiableMap(ret)).
                message("Failed to find related stock due to failed HTTP request").
                responseCode(companyInfos.getResponseCode()).build();
        }

        String unsanitizedCompanyListString = companyInfos.getContent();
        if (!unsanitizedCompanyListString.startsWith("[") || !unsanitizedCompanyListString.endsWith(
            "]")) {
            int startJson = unsanitizedCompanyListString.indexOf('[');
            int endJson = unsanitizedCompanyListString.lastIndexOf(']');
            unsanitizedCompanyListString = unsanitizedCompanyListString.substring(startJson,
                endJson + 1);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<String> unsanitizedCompanyList = new ArrayList<>();
            StringBuilder companyListBuilder = new StringBuilder();
            Optional<JsonNode> companies = Optional.of(
                mapper.readTree(unsanitizedCompanyListString));
            if (companies.isEmpty()) {
                logger.error(
                    "Failed to read related company data information: JSON mapper returned NULL!");
                return ResponseDto.builder(Collections.unmodifiableMap(ret)).
                    message(
                        "Failed to read related company data information: JSON mapper returned NULL!").
                    responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            companies.get().forEach(x -> unsanitizedCompanyList.add(x.get("company").asText()));
            sanitizeCompanyList(unsanitizedCompanyList).forEach(
                x -> companyListBuilder.append("[" + x + "] "));
            AlanResponseDto companyInvestmentAdvice = getResultFromAlan(
                SimpleAlanKoreanPromptBuilder.start().
                    addCommand("다음 주식 종목들에 대한 투자 조언을 해 줘.").
                    addContext("주식 종목들은 " + companyListBuilder.toString().trim()).
                    setOutputFormat("[{\"company_name\" : (주식 종목명), \"advice\" : (투자 조언)}]").
                    buildPrompt());
            String ia = companyInvestmentAdvice.getContent();
            if (!ia.startsWith("[") || !ia.endsWith("]")) {
                int startJson = ia.indexOf('[');
                int endJson = ia.lastIndexOf(']');
                ia = ia.substring(startJson, endJson + 1);
            }
            Optional.ofNullable(mapper.readTree(ia)).ifPresent(x -> x.forEach(y -> {
                String cName = Optional.ofNullable(y.get("company_name")).map(JsonNode::asText)
                    .orElse("null");
                String cAdvise = Optional.ofNullable(y.get("advice")).map(JsonNode::asText)
                    .orElse("null");
                if (!cName.equalsIgnoreCase("null") && !cAdvise.equalsIgnoreCase("null")) {
                    ret.put(cName, cAdvise);
                }
            }));
        } catch (JsonProcessingException jex) {
            logger.error("Failed to parse company data due to malformed JSON response!");
            return ResponseDto.builder(Collections.unmodifiableMap(ret)).
                message("Failed to parse company data due to malformed JSON response!").
                responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseDto.builder(Collections.unmodifiableMap(ret)).
            message("Successfully fetched related stocks.").
            responseCode(HttpStatus.OK).build();
    }

    public AlanResponseDto summarizeArticle(String articleUrl) {
        String translateQuery = new StringBuilder("이 URL에 있는 기사 자세히 요약해줘: ")
            .append(articleUrl)
            .append(
                " 그리고 이 기사의 대략적인 주제를 확인해 주고, 기사를 <정치, 경제, 사회, 생활문화, IT과학, 세계> 중에서 가장 적절한 카테고리로 분류해 줘.")
            .append(" 그리고 이 기사와 관련된 한국 국내 주식 종목들을 찾아 줘.")
            .append(" 답변은 다음 형식으로 해줘: \"")
            .append(
                "{ \"headline\" : (제목), \"content\" : (자세한 요약), \"category\" : (카테고리), \"topic\" : (대략적인 주제), \"companies\"  : [{\"company_name\"  : (종목명)}] }")
            .toString();
        return getResultFromAlan(translateQuery);
    }

    public ResponseDto<NewsSummaryItem> onlySummarizeArticle(String articleUrl) {
        String category = "";
        if (articleUrl.contains("sid=") && !articleUrl.endsWith("&sid=")) {
            switch (articleUrl.split("sid=")[1]) {
                case "100":
                    category = "정치";
                    break;
                case "101":
                    category = "경제";
                    break;
                case "102":
                    category = "사회";
                    break;
                case "103":
                    category = "생활문화";
                    break;
                case "104":
                    category = "세계";
                    break;
                case "105":
                    category = "IT과학";
                    break;
                case "106":
                    category = "생활문화";
                    break;
                case "107":
                    category = "생활문화";
                    break;
                default:
                    category = "";
                    break;
            }
        }
        String translateQuery = new StringBuilder("이 URL에 있는 기사 자세히 요약해줘: ")
            .append(articleUrl).toString();
        AlanResponseDto res = getResultFromAlan(translateQuery);

        NewsSummaryItem item = NewsSummaryItem.builder().
            content(res.getContent()).
            category(category).
            timestamp(getTimeInfoForArticle(articleUrl).getItem()).
            headline(getHeadlineForArticle(articleUrl).getItem()).link(articleUrl).build();
        return ResponseDto.builder(item).message(res.getResponseCode().toString())
            .responseCode(res.getResponseCode()).build();
    }

    public AlanResponseDto getInvestmentTactic(List<String> listOfArticles,
        List<String> listOfInterestedStocks) {
        StringBuilder articles = new StringBuilder();
        StringBuilder interestedStocks = new StringBuilder();
        listOfArticles.stream().map(x -> "<" + x + "> ").forEach(articles::append);
        listOfArticles.stream().map(x -> "<" + x + "> ").forEach(interestedStocks::append);
        return getResultFromAlan(
            SimpleAlanKoreanPromptBuilder.start().addCommand("가장 잘 맞는 투자전략을 추천해줘.")
                .addContext("최근에 읽은 기사들은 다음과 같아: " + articles.toString())
                .addContext("최근에 관심을 가진 종목들은 다움과 같아: " + interestedStocks.toString())
                .buildPrompt());
    }

    public AlanResponseDto sortTopic(String url) {
        return getResultFromAlan(SimpleAlanKoreanPromptBuilder.start()
            .addCommand("이 URL의 있는 기사는 어떤 카테고리로 분류하는 것이 가장 알맞을까?: ").addCommand(url)
            .addContext("카테고리의 목록은 다음과 같아. 다음 중 하나로 분류해줘: 정치, 경제, 사회, 생활문화, IT과학, 세계")
            .setOutputFormat("{ \"category\" : (분류한 카테고리), \"message\" : (분류한 이유) }")
            .addErrorHandler("{ \"category\" : \"알 수 없음\", \"message\" : (분류하지 못한 이유) }")
            .buildPrompt());
    }

    public List<String> sanitizeCompanyList(List<String> companyList) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<String> retList = new ArrayList<>();
            for (String currCompany : companyList) {
                String company_encoded = URLEncoder.encode(currCompany, "UTF-8");
                String r = restClient.get().
                    uri(URI.create(new StringBuilder("https://ac.stock.naver.com/ac?q=").append(
                        company_encoded).append("&target=stock").toString())).retrieve()
                    .body(String.class);
                JsonNode jsonRead = mapper.readTree(r);
                JsonNode itemsNode = Optional.of(jsonRead.get("items")).get();
                for (JsonNode j : itemsNode) {
                    String stockItemName = Optional.ofNullable(j.get("name")).map(JsonNode::asText)
                        .orElse("null");
                    String stockNationCode = Optional.ofNullable(j.get("nationCode"))
                        .map(JsonNode::asText).orElse("null");
                    String stockCategory = Optional.ofNullable(j.get("category"))
                        .map(JsonNode::asText).orElse("null");
                    if (!stockNationCode.equalsIgnoreCase("null") &&
                        stockNationCode.equalsIgnoreCase("KOR") &&
                        stockCategory.equalsIgnoreCase("stock")) {
                        retList.add(stockItemName);
                    }
                }
            }
            return retList.stream().distinct().toList();
        } catch (UnsupportedEncodingException e) {
            logger.error("Failed to parse company data due to encoding error!");
            return List.of();
        } catch (HttpStatusCodeException srex) {
            logger.error("Failed to parse company data due to network error!");
            return List.of();
        } catch (JsonProcessingException jex) {
            logger.error("Failed to parse company data due to malformed JSON response!");
            return List.of();
        } catch (NullPointerException nex) {
            logger.error("Failed to parse company data due to NULL value in JSON response data!");
            return List.of();
        }
    }

    public ResponseDto<String> getHeadlineForArticle(String url) {
        try {
            ResponseSpec res = restClient.get().uri(URI.create(url))
                .retrieve();
            String r = res.body(String.class);
            if (!r.contains("id=\"title_area\"")) {
                return ResponseDto.builder("")
                    .message("News article data does not contain valid headline data")
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            String[] headline1 = r.split("id=\"title_area\"");
            if (headline1.length < 2) {
                return ResponseDto.builder("").message(
                        "News article data does not contain valid headline data (invalid headline node)")
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            if (!headline1[1].contains("/h2>")) {
                return ResponseDto.builder("").message(
                        "News article data does not contain valid headline data (invalid h2 tag)")
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            String headline2 = headline1[1].split("/h2>")[0];
            if (!headline2.contains("<")) {
                return ResponseDto.builder("").message(
                        "News article data does not contain valid headline data (invalid h2 tag)")
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            String[] headline3 = headline2.split(">");
            StringBuilder headlineBuilder = new StringBuilder();
            for (String sc : headline3) {
                if (sc.contains("<")) {
                    String[] scs = sc.split("<");
                    if (scs.length != 0) {
                        headlineBuilder.append(scs[0].trim());
                    }
                }
            }
            return ResponseDto.builder(headlineBuilder.toString()).message("Success")
                .responseCode(HttpStatus.OK).build();
        } catch (HttpStatusCodeException hex) {
            return ResponseDto.builder("")
                .message("A HTTP fetch exception occurred while retrieving the headline data")
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseDto<LocalDateTime> getTimeInfoForArticle(String url) {
        LocalDateTime errorItem = LocalDateTime.now();
        ResponseSpec res = restClient.get().uri(URI.create(url))
            .retrieve();
        String r = res.body(String.class);
        if (!r.contains("data-date-time=\"")) {
            return ResponseDto.builder(errorItem)
                .message("News article's time data is not valid (no time delimiter)")
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        String[] dateTimeSplit = r.split("data-date-time=\"");
        if (dateTimeSplit.length < 2) {
            return ResponseDto.builder(errorItem)
                .message("News article's time data is not valid (nothing after time delimiter)")
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if (!dateTimeSplit[1].contains("\"")) {
            return ResponseDto.builder(errorItem)
                .message("News article's time data is not valid (timestamp quotation not complete)")
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        String dateTimeString = dateTimeSplit[1].split("\"")[0];
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return ResponseDto.builder(dateTime).message("Success").responseCode(HttpStatus.OK).build();
    }

    public ResponseDto<NewsDetailItem> getNewsDetails(String articleUrl) {
        ResponseDto<LocalDateTime> ts = getTimeInfoForArticle(articleUrl);
        if (ts.getResponseCode() != HttpStatus.OK) {
            logger.error("Failed to fetch time data for the article. The time data for the article "
                + articleUrl + " will not be valid");
        }
        AlanResponseDto newsSearchItem = summarizeArticle(articleUrl);
        if (newsSearchItem.getResponseCode() != HttpStatus.OK) {
            NewsDetailItem ret = NewsDetailItem.builder()
                .headline("Invalid response due to an error").content("").timestamp(ts.getItem())
                .category("").topic("").link(articleUrl).build();
            return ResponseDto.builder(ret).message("Invalid response due to an error")
                .responseCode(newsSearchItem.getResponseCode()).build();
        }
        ObjectMapper mapper = new ObjectMapper();
        String r = newsSearchItem.getContent();
        if (!r.startsWith("{") || !r.endsWith("}")) {
            int startJson = r.indexOf('{');
            int endJson = r.lastIndexOf('}');
            r = r.substring(startJson, endJson + 1);
        }
        try {
            JsonNode jsonRead = mapper.readTree(r);
            String headline = Optional.ofNullable(jsonRead.get("headline"))
                .map(x -> x.asText("null")).orElse("null");
            String content = Optional.ofNullable(jsonRead.get("content")).map(x -> x.asText("null"))
                .orElse("null");
            String topic = Optional.ofNullable(jsonRead.get("topic")).map(x -> x.asText("null"))
                .orElse("null");
            String category = Optional.ofNullable(jsonRead.get("category"))
                .map(x -> x.asText("null")).orElse("null");
            Optional<JsonNode> companies = Optional.ofNullable(jsonRead.get("companies"));

            if (headline.equalsIgnoreCase("null") ||
                content.equalsIgnoreCase("null") ||
                category.equalsIgnoreCase("null") ||
                companies.isEmpty() ||
                topic.equalsIgnoreCase("null")) {
                NewsDetailItem ret = NewsDetailItem.builder()
                    .headline("Malformed JSON. Failed to parse!").content(r).timestamp(ts.getItem())
                    .category("").topic("").link(articleUrl).build();
                return ResponseDto.builder(ret).message("Invalid response due to an error")
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            List<String> origCompanyList = new ArrayList<>();
            StringBuilder companyListBuilder = new StringBuilder();
            companies.get().forEach(x -> origCompanyList.add(x.get("company_name").asText()));
            sanitizeCompanyList(origCompanyList).forEach(
                x -> companyListBuilder.append("[" + x + "] "));
            AlanResponseDto companyInvestmentAdvice = getResultFromAlan(
                SimpleAlanKoreanPromptBuilder.start().
                    addCommand("다음 주식 종목들에 대한 투자 조언을 해 줘.").
                    addContext("주식 종목들은 " + companyListBuilder.toString().trim()).
                    setOutputFormat("[{\"company_name\" : (주식 종목명), \"advice\" : (투자 조언)}]").
                    buildPrompt());
            String ia = companyInvestmentAdvice.getContent();
            if (!ia.startsWith("[") || !ia.endsWith("]")) {
                int startJson = ia.indexOf('[');
                int endJson = ia.lastIndexOf(']');
                ia = ia.substring(startJson, endJson + 1);
            }
            Map<String, String> companyList = new LinkedHashMap<>();
            Optional.ofNullable(mapper.readTree(ia)).ifPresent(x -> x.forEach(y -> {
                String cName = Optional.ofNullable(y.get("company_name")).map(JsonNode::asText)
                    .orElse("null");
                String cAdvise = Optional.ofNullable(y.get("advice")).map(JsonNode::asText)
                    .orElse("null");
                if (!cName.equalsIgnoreCase("null") && !cAdvise.equalsIgnoreCase("null")) {
                    companyList.put(cName, cAdvise);
                }
            }));
            NewsDetailItem ret = NewsDetailItem.builder().headline(headline).content(content)
                .topic(topic).category(category).timestamp(ts.getItem())
                .relatedCompanies(Collections.unmodifiableMap(companyList)).link(articleUrl)
                .build();
            return ResponseDto.builder(ret)
                .message("Successfully summarized the article with details.")
                .responseCode(HttpStatus.OK).build();
        } catch (JsonProcessingException jex) {
            NewsDetailItem ret = NewsDetailItem.builder()
                .headline("Malformed JSON. Failed to parse!").timestamp(ts.getItem()).content(r)
                .category("").topic("").link(articleUrl).build();
            return ResponseDto.builder(ret).message("Invalid response due to an error")
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
