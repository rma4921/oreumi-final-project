import {fetchAndDisplayStock} from "./stock-viewer.js";

async function checkLoginStatus() {
    const response = await fetch('/api/user/status', { credentials: 'include' });
    if (response.ok) {
        const result = await response.json();
        return result.loggedIn; // true/false
    }
    return false;
}

document.addEventListener("DOMContentLoaded", async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const newsUrl = urlParams.get("news-url");

    if (!newsUrl) {
        alert("뉴스 URL이 제공되지 않았습니다.");
        return;
    }

    const isLoggedIn = await checkLoginStatus();

    try {
        const response = await fetch(
            `/api/v1/briefing/ai_detail?news-url=${encodeURIComponent(
                newsUrl)}`);
        const data = await response.json();

        if (data.item) {
            const headline = data.item.headline || "제목 없음";
            const content = data.item.content || "요약된 내용이 없습니다.";
            const topic = data.item.topic || "주제 없음";
            const category = data.item.category || "분류 없음";
            const link = data.item.link || "#";

            let relatedCompaniesHTML = "없음";
            if (typeof data.item.relatedCompanies === "object"
                && data.item.relatedCompanies !== null) {
                const entries = Object.entries(data.item.relatedCompanies);
                if (entries.length > 0) {
                    relatedCompaniesHTML = `
                        <ul>
                            ${entries.map(([name, reason]) => `
                                <li>
                                    <strong>${name}</strong>: ${reason}
                                    <button class="btn btn-sm btn-outline-secondary ml-2" data-company="${name}" style="background-color: #DCFDC7; border-color: #DCFDC7; color: #000;">
                                        주가 보기
                                    </button>
                                    <div class="stock-info mt-1" id="stock-${name.replace(
                        /\s/g, "")}"></div>
                                </li>
                            `).join("")}
                        </ul>
                    `;
                }
            }

            document.getElementById("headline").innerHTML = `
                ${headline}
                <button id="scrap-button" class="btn btn-sm ml-2" style="background-color: #DCFDC7; border-color: #DCFDC7; color: #000;">
                    스크랩하기
                </button>
            `;
            document.getElementById("scrap-button").addEventListener("click", async () => {
                if (!isLoggedIn) {
                    alert("로그인 후 이용해주세요!");
                    return;
                }

                // 관련 종목 이름 리스트 추출
                let relatedCompanies = [];
                if (typeof data.item.relatedCompanies === "object" && data.item.relatedCompanies !== null) {
                    relatedCompanies = Object.keys(data.item.relatedCompanies);
                }

                const scrapData = {
                    title: headline,
                    content: content,
                    topic: topic,
                    category: category,
                    link: link,
                    relatedCompanies: relatedCompanies
                };

                try {
                    const scrapResponse = await fetch('/api/scrap', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${token}`
                        },
                        body: JSON.stringify(scrapData)
                    });

                    if (scrapResponse.ok) {
                        alert("스크랩 성공!");
                    } else {
                        alert("스크랩 실패!");
                    }
                } catch (error) {
                    console.error("스크랩 요청 실패:", error);
                    alert("스크랩 요청 중 오류 발생");
                }
            });

            const container = document.querySelector(".container");
            const contentElement = document.createElement("div");
            contentElement.innerHTML = `
                <p><strong>분류:</strong> ${category}</p>
                <p><strong>주제:</strong> ${topic}</p>
                <p class="mt-3">${content}</p>
                <p><strong>관련 종목:</strong></p>
                ${relatedCompaniesHTML}
                <div class="mt-3">
                    <a class="btn btn-sm" href="${link}" target="_blank" style="background-color: #DCFDC7; border-color: #DCFDC7; color: #000;">
                        원문 기사 보기
                    </a>
                </div>
                <hr/>
        `;
            container.insertBefore(contentElement,
                document.getElementById("comment-list"));

            // 주가 보기 버튼
            container.querySelectorAll("button[data-company]").forEach(
                button => {
                    button.addEventListener("click", () => {
                        const companyName = button.dataset.company.trim();
                        const targetId = `stock-${companyName.replace(/\s/g,
                            "")}`;
                        fetchAndDisplayStock(companyName, targetId);
                    });
                });

        } else {
            document.getElementById("headline").textContent = "기사를 불러오지 못했습니다.";
        }
    } catch (err) {
        console.error("AI 요약 정보 요청 실패:", err);
        alert("AI 요약 정보를 불러오는 데 실패했습니다.");
    }

});

