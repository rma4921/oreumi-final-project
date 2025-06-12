export async function fetchAndDisplayStock(companyName, targetElementId) {
    const stockDiv = document.getElementById(targetElementId);
    stockDiv.innerHTML = "<b>불러오는 중입니다. 잠시만 기다려주세요!</b>";

    try {
        const response = await fetch(`/api/stock-price/by-name?name=${encodeURIComponent(companyName)}&count=10`);
        const data = await response.json();

        if (data && Array.isArray(data.item) && data.item.length > 0) {
            const prices = data.item.map(p => {
                const date = formatDate(p.baseDate);
                return `${date} - ${p.price.toLocaleString()}원 (${p.fluctuationRate}%)`;
            }).join("<br/>");

            stockDiv.innerHTML = `<div class="mt-2"><strong>최근 10일 주가:</strong><br>${prices}</div><br>`;
        } else {
            stockDiv.innerHTML = "<b>주가 정보를 찾을 수 없습니다.</b>";
        }
    } catch (err) {
        console.error("주가 요청 실패:", err);
        stockDiv.innerHTML = "주가 정보를 불러오지 못했습니다.";
    }
}

function formatDate(dateString) {
    if (!dateString || dateString.length !== 8) return dateString;
    const year = dateString.slice(0, 4);
    const month = dateString.slice(4, 6);
    const day = dateString.slice(6, 8);
    return `${year}년 ${month}월 ${day}일`;
}