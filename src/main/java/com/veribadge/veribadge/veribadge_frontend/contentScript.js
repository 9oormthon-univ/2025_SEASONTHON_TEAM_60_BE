console.log("[VeriBadge] content script loaded");

/* -------------------- 1) CSS 주입: 한 번만 -------------------- */
(function injectStyleOnce() {
    if (document.getElementById("veribadge-style")) return;
    const style = document.createElement("style");
    style.id = "veribadge-style";
    style.textContent = `
  .veribadge-wrapper {
    display: inline-block;      /* flex 대신 inline-block로 라인 안정화 */
    vertical-align: text-bottom;
    font-size: 0;               /* 내부 공백/개행이 줄을 밀지 못하게 */
    white-space: nowrap;        /* 래퍼 내부 줄바꿈 방지 */
    line-height: 1;
  }
  .veribadge-img {
    display: block;             /* 래퍼 내부 꽉 채우기 */
    width: 20px;
    height: 20px;
    margin-left: 4px;
    cursor: pointer;
    object-fit: contain;
  }
  /* 포털(=body)에 붙는 툴팁 */
  .veribadge-portal-tooltip {
    position: fixed;
    background: rgba(34,34,34,0.96);
    color: #fff;
    font-size: 12px;
    padding: 8px 10px;
    border-radius: 8px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.22);
    white-space: nowrap;
    z-index: 2147483647; /* 최대 z-index */
    pointer-events: auto;
  }
  .veribadge-portal-tooltip .veribadge-badge-icon {
    width: 14px;
    height: 14px;
    object-fit: contain;
    vertical-align: text-bottom;
    margin-right: 6px;
  }
  `;
    document.head.appendChild(style);
})();

/* -------------------- 2) 유틸 -------------------- */
function getBadgeImageUrlByTier(tier) {
    switch (tier?.toLowerCase()) {
        case "silver":   return chrome.runtime.getURL("images/silver.png");
        case "gold":     return chrome.runtime.getURL("images/gold.png");
        case "platinum": return chrome.runtime.getURL("images/platinum.png");
        case "diamond":  return chrome.runtime.getURL("images/diamond.png");
        case "doctor":   return chrome.runtime.getURL("images/doctor.png");
        default:         return chrome.runtime.getURL("images/default.png");
    }
}

function createPortalTooltip({ badgeImageUrl, tier, verifiedDate }) {
    const ko = { silver:"실버", gold:"골드", platinum:"플래티넘", diamond:"다이아몬드", doctor:"의사" };
    const tip = document.createElement("div");
    tip.className = "veribadge-portal-tooltip";
    tip.innerHTML = `
    <div style="margin-bottom:4px;">
      ${badgeImageUrl ? `<img src="${badgeImageUrl}" class="veribadge-badge-icon" alt="badge" />` : ""}
      인증 정보
    </div>
    <div>자격: ${ko[tier?.toLowerCase()] ?? tier}</div>
    ${verifiedDate ? `<div>인증일: ${verifiedDate}</div>` : ""}
    <div>확인: veribadge.com</div>
  `;
    document.body.appendChild(tip);
    return tip;
}

function positionTooltip(tip, anchorEl) {
    const rect = anchorEl.getBoundingClientRect();
    const margin = 8;
    let left = rect.left;
    let top  = rect.bottom + margin;

    const iw = window.innerWidth;
    const ih = window.innerHeight;

    tip.style.visibility = "hidden";
    tip.style.left = "0px";
    tip.style.top  = "0px";
    tip.style.display = "block";

    const tw = tip.offsetWidth;
    const th = tip.offsetHeight;

    if (left + tw > iw - 8) left = iw - tw - 8;
    if (top + th > ih - 8)  top  = rect.top - th - margin; // 아래 부족하면 위로

    tip.style.left = `${left}px`;
    tip.style.top  = `${top}px`;
    tip.style.visibility = "visible";
}

function attachPortalTooltip(anchorEl, { badgeImageUrl, tier, verifiedDate }) {
    let tip = null, hideTimer = null;

    const show = () => {
        if (!tip) tip = createPortalTooltip({ badgeImageUrl, tier, verifiedDate });
        clearTimeout(hideTimer);
        tip.style.display = "block";
        positionTooltip(tip, anchorEl);
        const update = () => tip && positionTooltip(tip, anchorEl);
        tip.__update = update;
        window.addEventListener("scroll", update, true);
        window.addEventListener("resize", update, true);
    };

    const scheduleHide = () => {
        hideTimer = setTimeout(() => {
            if (!tip) return;
            tip.style.display = "none";
            window.removeEventListener("scroll", tip.__update, true);
            window.removeEventListener("resize", tip.__update, true);
        }, 120);
    };

    anchorEl.addEventListener("mouseenter", show);
    anchorEl.addEventListener("mouseleave", scheduleHide);

    // 툴팁 위로 이동해도 유지
    document.addEventListener("mousemove", (e) => {
        if (!tip || tip.style.display === "none") return;
        const overIcon = anchorEl.contains(e.target);
        const overTip  = tip.contains(e.target);
        if (overIcon || overTip) clearTimeout(hideTimer);
        else scheduleHide();
    });
}

/* -------------------- 3) 태그 → 배지 치환 (텍스트 노드 단위) -------------------- */
function replaceTagWithBadge(commentTextElem, tag, badgeImageUrl, verifiedDate, tier) {
    const walker = document.createTreeWalker(
        commentTextElem,
        NodeFilter.SHOW_TEXT,
        {
            acceptNode(node) {
                return node.nodeValue && node.nodeValue.includes(tag)
                    ? NodeFilter.FILTER_ACCEPT
                    : NodeFilter.FILTER_SKIP;
            }
        }
    );

    const insertedIcons = [];
    while (walker.nextNode()) {
        const textNode = walker.currentNode;
        const idx = textNode.nodeValue.indexOf(tag);
        if (idx === -1) continue;

        const before = textNode.nodeValue.slice(0, idx);
        const after  = textNode.nodeValue.slice(idx + tag.length);

        const wrap = document.createElement("span");
        wrap.className = "veribadge-wrapper";
        const img = document.createElement("img");
        img.className = "veribadge-img";
        img.src = badgeImageUrl;
        img.alt = "VeriBadge";
        wrap.appendChild(img);

        const frag = document.createDocumentFragment();
        if (before) frag.appendChild(document.createTextNode(before));
        frag.appendChild(wrap);
        if (after)  frag.appendChild(document.createTextNode(after));

        textNode.parentNode.replaceChild(frag, textNode);
        insertedIcons.push(img);
    }

    // 툴팁 연결
    for (const img of insertedIcons) {
        attachPortalTooltip(img, { badgeImageUrl, tier, verifiedDate });
    }
}

/* -------------------- 4) 태그/채널 추출 + 백엔드 -------------------- */
function extractVeriTagAndTier(text) {
    const re = /@veri-(silver|gold|platinum|diamond|doctor)-[a-z0-9]{6}/i;
    const m = text.match(re);
    return m ? { tag: m[0], tier: m[1] } : null;
}

function extractChannelUrlFromNode(node) {
    const anchor = node.querySelector(
        'a#author-text, ytd-channel-name a, a.yt-simple-endpoint[href^="/@"], a.yt-simple-endpoint[href^="/channel/"]'
    );
    if (!anchor) return null;

    const href = anchor.getAttribute("href") || "";
    if (href.startsWith("/@")) {
        const encoded = href.slice(2).split(/[/?#]/)[0];
        const handle = decodeURIComponent(encoded);
        return `www.youtube.com/@${handle}`;
    }
    return null; // /channel/ 형식은 현재 정책상 미사용
}

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
            if (!res.ok) continue;
            const json = await res.json();
            return json?.data || null;
        } catch (_) {}
    }
    return null;
}

/* -------------------- 5) 파이프라인 -------------------- */
const processed = new Set();

async function handleCommentNode(node) {
    const commentTextElem =
        node.querySelector("#content-text") ||
        node.querySelector("yt-attributed-string#content-text") ||
        node.querySelector("yt-attributed-string");
    if (!commentTextElem) return;

    const txt = commentTextElem.innerText || "";
    const found = extractVeriTagAndTier(txt);
    if (!found) return;

    const commentId =
        node.getAttribute("data-id") ||
        node.querySelector('[id^="comment"]')?.id ||
        `c_${Math.random().toString(36).slice(2)}`;
    if (processed.has(commentId)) return;

    const channelUrl = extractChannelUrlFromNode(node);
    if (!channelUrl) return;

    processed.add(commentId);

    const { tag } = found;
    const verify = await verifyWithBackend({ tag, channelUrl });
    if (!verify || !verify.valid) return;

    const badgeImageUrl = getBadgeImageUrlByTier(verify.badgeLevel);
    if (!badgeImageUrl) return;

    replaceTagWithBadge(
        commentTextElem,
        tag,
        badgeImageUrl,
        verify.verifiedDate,
        verify.badgeLevel
    );
}

function scanExistingComments() {
    document
        .querySelectorAll("ytd-comment-thread-renderer, ytd-comment-view-model")
        .forEach((n) => handleCommentNode(n));
}

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

(function start() {
    const root = document.querySelector("#comments") || document.body;
    observer.observe(root, { childList: true, subtree: true });
    scanExistingComments();
    console.log("[VeriBadge] 교차검증 + 인라인 배지 + 포털 툴팁 활성");
})();
