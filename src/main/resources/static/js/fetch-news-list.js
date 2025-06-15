async function getContents() {
    const mainPageDiv = document.getElementById("main-page-news-section");
    try {
        const response = await fetch(`/api/v1/briefing/latest`);
        const data = await response.json();

        if (data && Array.isArray(data.item) && data.item.length > 0) {
            for (x of data.item) {
                let itemToAdd = document.createElement("div");
                let headline = document.createElement("a");
                let summary = document.createElement("div");
                let timestamp = document.createElement("div");
                itemToAdd.classList.add("news-item")
                headline.innerHTML = x.headline;
                headline.setAttribute("href", `/news/detail?news-url=${encodeURIComponent(x.refLink)}`);
                summary.classList.add("news-topic");
                summary.innerHTML = x.summary;
                timestamp.classList.add("news-date");
                timestamp.textContent = formatDate(x.timestamp);
                
                itemToAdd.appendChild(headline);
                itemToAdd.appendChild(summary);
                itemToAdd.appendChild(document.createElement("br"));
                itemToAdd.appendChild(timestamp);
                mainPageDiv.appendChild(itemToAdd);
            }
        } else {
            console.error("Invalid response: The news briefing API did not return any article!")
        }
    } catch (err) {
        console.error("An error occurred while fetching and parsing the news briefing data: ", err);
    }
}

function formatDate(dateString) {
    if (!dateString || dateString.length !== 8) return dateString;
    const year = dateString.slice(0, 4);
    const month = dateString.slice(4, 6);
    const day = dateString.slice(6, 8);
    return `${year}년 ${month}월 ${day}일`;
}

getContents();