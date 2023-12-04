const onHoverElement = document.getElementById('account-section-header');

// On width change, change width of new element
onHoverElement.addEventListener('mouseover', function() {
    document.getElementById('account-popup').style.width = onHoverElement.offsetWidth - 2 + 'px';
});