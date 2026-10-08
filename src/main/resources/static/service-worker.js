const CACHE_NAME = "times-up-v1";

const FILES_TO_CACHE = [
    "/",
    "/index.html",
    "/style.css",
    "/app.js",
    "/manifest.json"
];

self.addEventListener("install", event => {

    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => cache.addAll(FILES_TO_CACHE))
    );

    self.skipWaiting();
});


self.addEventListener("activate", event => {

    event.waitUntil(
        caches.keys().then(cacheNames => {

            return Promise.all(
                cacheNames
                    .filter(name => name !== CACHE_NAME)
                    .map(name => caches.delete(name))
            );

        })
    );

    self.clients.claim();
});


self.addEventListener("fetch", event => {

    // API zawsze pobieramy normalnie z backendu
    if (event.request.url.includes("/api/")) {
        return;
    }

    event.respondWith(
        caches.match(event.request)
            .then(cachedResponse => {

                return cachedResponse || fetch(event.request);

            })
    );
});