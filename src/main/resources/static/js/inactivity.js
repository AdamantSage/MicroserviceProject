let warningTimeout;
let logoutTimeout;

function resetTimers() {
  clearTimeout(warningTimeout);
  clearTimeout(logoutTimeout);

  // Show warning after 15 seconds of inactivity
  warningTimeout = setTimeout(() => {
    const modal = new bootstrap.Modal(document.getElementById('sessionTimeoutModal'));
    modal.show();

    // Logout after 10 more seconds if no response
    logoutTimeout = setTimeout(() => {
      window.location.href = '/logout';
    }, 10000);
  }, 15000);
}

document.addEventListener('mousemove', resetTimers);
document.addEventListener('keypress', resetTimers);
document.addEventListener('scroll', resetTimers);
document.addEventListener('click', resetTimers);

// Handle "Extend Session"
document.addEventListener('DOMContentLoaded', function () {
  resetTimers();

  document.getElementById('extendSessionBtn').addEventListener('click', function () {
    fetch('/extend-session', { method: 'POST' })
      .then(() => {
        resetTimers();
        bootstrap.Modal.getInstance(document.getElementById('sessionTimeoutModal')).hide();
      });
  });
});
