<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html><html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="theme-color" content="#183e35"><title>Serene — Hotel workspace</title><link rel="icon" href="assets/favicon.svg" type="image/svg+xml"><link rel="stylesheet" href="assets/styles.css"><script src="assets/modules/guests.js" defer></script>
<script src="assets/modules/rooms.js" defer></script>
<script src="assets/modules/reservations.js" defer></script>
<script src="assets/modules/staff.js" defer></script>
<script src="assets/modules/services.js" defer></script>
<script src="assets/modules/reviews.js" defer></script>
<script src="assets/app.js" defer></script></head>
<body><div id="app"><div class="loading">Preparing your workspace…</div></div><div id="toasts" role="status" aria-live="polite"></div><dialog id="confirm-dialog"><form method="dialog"><span class="eyebrow">PLEASE CONFIRM</span><h2 id="confirm-title">Delete this record?</h2><p id="confirm-copy">This action permanently removes the record.</p><div class="form-actions"><button value="cancel" class="button secondary">Keep record</button><button value="delete" class="button danger">Delete record</button></div></form></dialog></body></html>
