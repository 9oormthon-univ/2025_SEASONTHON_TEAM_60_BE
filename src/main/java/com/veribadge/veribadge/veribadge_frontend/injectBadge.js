export function addBadgeToCommentElement(commentElement, badgeUrl) {
    if (!commentElement.querySelector('.veribadge-img')) {
        const img = document.createElement('img');
        img.src = badgeUrl;
        img.alt = 'VeriBadge';
        img.className = 'veribadge-img';
        img.style.marginLeft = '8px';
        img.style.width = '20px';
        img.style.height = '20px';

        commentElement.appendChild(img);
    }
}