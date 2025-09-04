// injectBadge.js
// 댓글 텍스트 엘리먼트 안의 '@veri-...'를 배지로 교체하고, hover 툴팁(포털)을 연결

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
        attachPortalTooltip(img, {
            badgeImageUrl,
            tier: tooltipData?.tier,
            verifiedDate: tooltipData?.verifiedDate
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

/* ---------- 내부 유틸: 포털 툴팁 ---------- */
function attachPortalTooltip(anchorEl, { badgeImageUrl, tier, verifiedDate }) {
    let tip = null, hideTimer = null;

    const makeTip = () => {
        const ko = { silver:"실버", gold:"골드", platinum:"플래티넘", diamond:"다이아몬드", doctor:"의사" };
        const el = document.createElement("div");
        el.className = "veribadge-portal-tooltip";
        el.innerHTML = `
      <div style="margin-bottom:4px;">
        ${badgeImageUrl ? `<img src="${badgeImageUrl}" class="veribadge-badge-icon" alt="badge" />` : ""}
        인증 정보
      </div>
      ${tier ? `<div>자격: ${ko[tier?.toLowerCase()] ?? tier}</div>` : ""}
      ${verifiedDate ? `<div>인증일: ${verifiedDate}</div>` : ""}
      <div>확인: veribadge.com</div>`;
        document.body.appendChild(el);
        return el;
    };

    const position = () => {
        const rect = anchorEl.getBoundingClientRect();
        let left = rect.left, top = rect.bottom + 8;
        const iw = window.innerWidth, ih = window.innerHeight;

        tip.style.visibility = "hidden";
        tip.style.display = "block";
        const tw = tip.offsetWidth, th = tip.offsetHeight;

        if (left + tw > iw - 8) left = iw - tw - 8;
        if (top + th > ih - 8)  top  = rect.top - th - 8;

        tip.style.left = `${left}px`;
        tip.style.top  = `${top}px`;
        tip.style.visibility = "visible";
    };

    const show = () => {
        if (!tip) tip = makeTip();
        clearTimeout(hideTimer);
        tip.style.display = "block";
        position();
        tip.__update = position;
        window.addEventListener("scroll", position, true);
        window.addEventListener("resize", position, true);
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
        const overTip  = tip.contains(e.target);
        if (overIcon || overTip) clearTimeout(hideTimer);
        else scheduleHide();
    });
}

export { attachPortalTooltip }; // 필요 시 외부에서도 사용 가능
