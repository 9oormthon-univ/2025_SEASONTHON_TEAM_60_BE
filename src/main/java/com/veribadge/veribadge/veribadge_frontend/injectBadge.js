// injectBadge.js: 댓글 DOM에 badge 이미지와 hover 툴팁을 삽입하는 UI 모듈 (재사용 가능한 단일 기능)
// contentScript.js: YouTube 댓글창에 대해 injectBadge.js를 호출하여 전체 흐름 제어 + 백엔드 연동 포함 (실제 실행 담당)

export function injectBadgeIntoComment({
                                           commentTextElem,   // 필수: 댓글 텍스트 DOM (예: #content-text)
                                           tag,               // 필수: 치환할 태그 문자열 (예: '@veri-gold-ab12cd')
                                           badgeImageUrl,     // 필수: 아이콘 URL (chrome.runtime.getURL(...))
                                           tooltipData        // 선택: { tier: 'gold', verifiedDate: '2025.09.03' }
                                       }) {
    if (!commentTextElem || !tag || !badgeImageUrl) return;

    ensureVeriBadgeStyle();

    // Text 노드 단위 교체
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

    const icons = [];
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

        icons.push(img);
    }

    // 툴팁 연결
    for (const img of icons) {
        console.log("[injectBadge] tooltipData 전달 확인:", {
            tier: tooltipData?.tier,
            verifiedDate: tooltipData?.verifiedDate,
            description: tooltipData?.description,
        });
        attachPortalTooltip(img, {
            badgeImageUrl,
            tier: tooltipData?.tier,
            verifiedDate: tooltipData?.verifiedDate,
            description: tooltipData?.description
        });
    }
}

/* ---------- 내부 유틸: CSS 주입(중복 방지) ---------- */
function ensureVeriBadgeStyle() {
    if (document.getElementById("veribadge-style")) return;
    const style = document.createElement("style");
    style.id = "veribadge-style";
    style.textContent = `
  .veribadge-wrapper {
    display: inline-block;
    vertical-align: text-bottom;
    font-size: 0;
    white-space: nowrap;
    line-height: 1;
  }
  .veribadge-img {
    display: block;
    width: 20px !important;
    height: 20px !important;
    margin-left: 4px;
    object-fit: contain;
    cursor: pointer;
  }
  .veribadge-portal-tooltip {
    position: fixed;
    background: rgba(34,34,34,0.96);
    color: #fff;
    font-size: 12px;
    padding: 8px 10px;
    border-radius: 8px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.22);
    white-space: nowrap;
    z-index: 2147483647;
    pointer-events: auto;
  }
  .veribadge-portal-tooltip .veribadge-badge-icon {
    width: 14px; height: 14px; margin-right: 6px; vertical-align: text-bottom; object-fit: contain;
  }`;
    document.head.appendChild(style);
}

function getBadgeLabel(tier) {
    switch (tier?.toUpperCase()) {
        case "GOLD": return "상위 20%";
        case "PLATINUM": return "상위 10%";
        case "DIAMOND": return "상위 1%";
        case "DOCTOR": return "의사 인증";
        default: return tier ?? "등급 미확인";
    }
}

function createPortalTooltip({ badgeImageUrl, tier, verifiedDate, description }) {
    console.log("[createPortalTooltip] 받은 description:", description);
    const tip = document.createElement("div");
    tip.className = "veribadge-portal-tooltip";
    tip.innerHTML = `
    <div style="margin-bottom:4px;">
      ${badgeImageUrl ? `<img src="${badgeImageUrl}" class="veribadge-badge-icon" alt="badge" />` : ""}
      인증정보
    </div>
    <div>자격: ${getBadgeLabel(tier)}</div>
    ${description ? `<div>${description}</div>` : ""}
    ${verifiedDate ? `<div>인증일: ${verifiedDate}</div>` : ""}
    <div>
      <a href="https://two025-seasonthon-team-60-be.onrender.com" target="_blank" style="color:#00bfff;text-decoration:underline;">
        확인: veribadge.com
      </a>
    </div>
  `;
    document.body.appendChild(tip);
    return tip;
}

function positionTooltip(tip, anchorEl) {
    const rect = anchorEl.getBoundingClientRect();
    let left = rect.left;
    let top = rect.bottom + 8;
    const iw = window.innerWidth;
    const ih = window.innerHeight;

    tip.style.visibility = "hidden";
    tip.style.display = "block";
    const tw = tip.offsetWidth;
    const th = tip.offsetHeight;

    if (left + tw > iw - 8) left = iw - tw - 8;
    if (top + th > ih - 8) top = rect.top - th - 8;

    tip.style.left = `${left}px`;
    tip.style.top = `${top}px`;
    tip.style.visibility = "visible";
}

function attachPortalTooltip(anchorEl, data) {
    let tip = null;
    let hideTimer = null;

    const show = () => {
        if (!tip) tip = createPortalTooltip(data);
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

    document.addEventListener("mousemove", (e) => {
        if (!tip || tip.style.display === "none") return;
        const overIcon = anchorEl.contains(e.target);
        const overTip = tip.contains(e.target);
        if (overIcon || overTip) clearTimeout(hideTimer);
        else scheduleHide();
    });
}
