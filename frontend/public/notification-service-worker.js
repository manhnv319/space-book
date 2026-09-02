self.addEventListener("push", (event) => {
  const payload = event.data ? event.data.json() : {};
  const targetPath = typeof payload.targetPath === "string" && payload.targetPath.startsWith("/") && !payload.targetPath.startsWith("//") ? payload.targetPath : "/";
  event.waitUntil(self.registration.showNotification(payload.title || "Sách Nhà", { body: payload.body || "Bạn có thông báo mới.", data: { targetPath } }));
});

self.addEventListener("notificationclick", (event) => {
  event.notification.close();
  const targetPath = event.notification.data && event.notification.data.targetPath ? event.notification.data.targetPath : "/";
  event.waitUntil(clients.openWindow(new URL(targetPath, self.location.origin).toString()));
});
