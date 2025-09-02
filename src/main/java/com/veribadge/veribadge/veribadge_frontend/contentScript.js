console.log("VeriBadge 확장 기능 실행됨");

// 1) 티어 → 이미지 URL 매핑
function getBadgeUrlByTier(tier) {
    switch (tier?.toLowerCase()) {
        case "silver":   return chrome.runtime.getURL("images/silver.png");
        case "gold":     return chrome.runtime.getURL("images/gold.png");
        case "platinum": return chrome.runtime.getURL("images/platinum.png");
        case "diamond":  return chrome.runtime.getURL("images/diamond.png");
        default:         return null;
    }
}

// 2) 댓글에서 Veri 태그 1개 추출
function extractVeriTagAndTier(text) {
    const re = /@veri-(silver|gold|platinum|diamond)-[a-z0-9]{6}/i;
    const m = text.match(re);
    if (!m) return null;
    const tag = m[0];   // 예: @veri-gold-abc123
    const tier = m[1];  // 예: gold
    return { tag, tier };
}

// 3) Veri 태그 문자열을 배지 이미지로 치환
function replaceTagWithBadge(commentTextElem, tag, badgeUrl) {
    if (!badgeUrl) return;

    const html = commentTextElem.innerHTML;
    const safeTag = tag.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // 정규식 이스케이프
    const re = new RegExp(safeTag, 'g');

    const imgHtml = `<img class="veribadge-img" src="${badgeUrl}" alt="VeriBadge" title="소득 인증 뱃지" style="width:20px;height:20px;margin-left:6px;vertical-align:text-bottom;">`;

    const newHtml = html.replace(re, imgHtml);
    if (newHtml !== html) {
        commentTextElem.innerHTML = newHtml;
    }
}

// 4) 채널 URL 추출: www.youtube.com/@핸들 형식 반환
function extractChannelUrlFromNode(node) {
    const anchor = node.querySelector(
        'a#author-text, ytd-channel-name a, a.yt-simple-endpoint[href^="/@"], a.yt-simple-endpoint[href^="/channel/"]'
    );
    if (!anchor) return null;

    const href = anchor.getAttribute('href') || '';
    if (href.startsWith('/@')) {
        const encodedHandle = href.slice(2).split(/[/?#]/)[0];
        const handle = decodeURIComponent(encodedHandle);
        return `www.youtube.com/@${handle}`;
    }

    return null; // /channel/UC... 형태는 무시
}

// 5) 백엔드에 태그+채널 교차검증 요청 (localhost + 127 fallback)
async function verifyWithBackend({ tag, channelUrl }) {
    const endpoints = [
        "http://localhost:8080/api/badge/verify",
        "http://127.0.0.1:8080/api/badge/verify"
    ];

    for (const url of endpoints) {
        try {
            const res = await fetch(url, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ tag, channelUrl })
            });
            if (!res.ok) {
                console.warn("[VeriBadge] backend non-2xx:", res.status, await res.text());
                continue;
            }
            const json = await res.json();
            return json?.data || null;
        } catch (e) {
            console.warn("[VeriBadge] backend fetch failed:", url, e);
        }
    }

    return null;
}

// 6) 댓글 1개 처리
const processedComments = new Set();

async function handleCommentNode(node) {
    const commentTextElem =
        node.querySelector("#content-text") ||
        node.querySelector("yt-attributed-string#content-text") ||
        node.querySelector("yt-attributed-string");

    if (!commentTextElem) return;

    const text = commentTextElem.innerText || "";
    const found = extractVeriTagAndTier(text);
    if (!found) return;

    const commentId =
        node.getAttribute("data-id") ||
        node.querySelector('[id^="comment"]')?.id ||
        `c_${Math.random().toString(36).slice(2)}`;

    if (processedComments.has(commentId)) return;

    const channelUrl = extractChannelUrlFromNode(node);
    if (!channelUrl) return;

    processedComments.add(commentId);

    const { tag } = found;
    const verify = await verifyWithBackend({ tag, channelUrl });

    if (!verify || !verify.valid) return;

    const badgeUrl = getBadgeUrlByTier(verify.badgeLevel);
    if (!badgeUrl) return;

    replaceTagWithBadge(commentTextElem, tag, badgeUrl);
}

// 7) 기존 댓글 초기 스캔
function scanExistingComments() {
    document
        .querySelectorAll("ytd-comment-thread-renderer, ytd-comment-view-model")
        .forEach((n) => handleCommentNode(n));
}

// 8) DOM 변경 감지
const observer = new MutationObserver((mutations) => {
    for (const m of mutations) {
        for (const node of m.addedNodes) {
            if (node.nodeType !== Node.ELEMENT_NODE) continue;

            if (
                node.matches?.("ytd-comment-thread-renderer, ytd-comment-view-model") ||
                node.querySelector?.("ytd-comment-thread-renderer, ytd-comment-view-model")
            ) {
                handleCommentNode(node);
                node
                    .querySelectorAll?.("ytd-comment-thread-renderer, ytd-comment-view-model")
                    .forEach((n) => handleCommentNode(n));
            }
        }
    }
});

// 9) 실행 시작
function start() {
    const root = document.querySelector("#comments") || document.body;
    observer.observe(root, { childList: true, subtree: true });
    scanExistingComments();
    console.log("[VeriBadge] 태그+채널URL → 뱃지 검증 시작됨");
}

start();