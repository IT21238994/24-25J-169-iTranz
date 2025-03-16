const { db } = require("../config/firebaseConfig");

const routeData = {
  "Panadura-Kandy": {
    busStops: [
      { "stopNumber": 0, "stopName": "Panadura", "fare": 0 },
      { "stopNumber": 1, "stopName": "Aluth Palama", "fare": 27 },
      { "stopNumber": 2, "stopName": "Egodauyana", "fare": 35 },
      { "stopNumber": 3, "stopName": "Koralawalla", "fare": 45 },
      { "stopNumber": 4, "stopName": "Moratuwa", "fare": 56 },
      { "stopNumber": 5, "stopName": "Katubadda", "fare": 66 },
      { "stopNumber": 6, "stopName": "Ratmalana", "fare": 77 },
      { "stopNumber": 7, "stopName": "Galkissa", "fare": 87 },
      { "stopNumber": 8, "stopName": "Dehiwala", "fare": 90 },
      { "stopNumber": 9, "stopName": "Miris Mola", "fare": 97 },
      { "stopNumber": 10, "stopName": "Kalubowila", "fare": 104 },
      { "stopNumber": 11, "stopName": "Nugegoda", "fare": 111 },
      { "stopNumber": 12, "stopName": "Pitakotte", "fare": 117 },
      { "stopNumber": 13, "stopName": "Athulkotte", "fare": 123 },
      { "stopNumber": 14, "stopName": "Battaramulla", "fare": 130 },
      { "stopNumber": 15, "stopName": "Koswatta Junction", "fare": 136 },
      { "stopNumber": 16, "stopName": "Malabe", "fare": 141 },
      { "stopNumber": 17, "stopName": "Pittugala", "fare": 148 },
      { "stopNumber": 18, "stopName": "Kothalawala", "fare": 153 },
      { "stopNumber": 19, "stopName": "Kaduwela", "fare": 160 },
      { "stopNumber": 20, "stopName": "Malwana", "fare": 165 },
      { "stopNumber": 21, "stopName": "Biyagama", "fare": 172 },
      { "stopNumber": 22, "stopName": "Siyabalape", "fare": 177 },
      { "stopNumber": 23, "stopName": "Delgoda", "fare": 184 },
      { "stopNumber": 24, "stopName": "Naranwila", "fare": 190 },
      { "stopNumber": 25, "stopName": "Ambarumluwa Temple", "fare": 197 },
      { "stopNumber": 26, "stopName": "Weliweriya", "fare": 202 },
      { "stopNumber": 27, "stopName": "Rathupaswala", "fare": 209 },
      { "stopNumber": 28, "stopName": "Miriswatta", "fare": 214 },
      { "stopNumber": 29, "stopName": "Yakkala", "fare": 221 },
      { "stopNumber": 30, "stopName": "Aluthgama", "fare": 228 },
      { "stopNumber": 31, "stopName": "Kalagedihena", "fare": 233 },
      { "stopNumber": 32, "stopName": "Thihariya", "fare": 240 },
      { "stopNumber": 33, "stopName": "Nittambuwa Sangha Maha Vidyalaya", "fare": 245 },
      { "stopNumber": 34, "stopName": "Nittambuwa", "fare": 252 },
      { "stopNumber": 35, "stopName": "Kalalpitiya", "fare": 257 },
      { "stopNumber": 36, "stopName": "Pasyala", "fare": 264 },
      { "stopNumber": 37, "stopName": "Kajugama", "fare": 270 },
      { "stopNumber": 38, "stopName": "Radawadunna", "fare": 277 },
      { "stopNumber": 39, "stopName": "Wewaldeniya", "fare": 282 },
      { "stopNumber": 40, "stopName": "Danowita", "fare": 289 },
      { "stopNumber": 41, "stopName": "Dummaladeniya", "fare": 294 },
      { "stopNumber": 42, "stopName": "Warakapola", "fare": 301 },
      { "stopNumber": 43, "stopName": "Ambepussa", "fare": 306 },
      { "stopNumber": 44, "stopName": "Maahena", "fare": 313 },
      { "stopNumber": 45, "stopName": "Tholangamuwa", "fare": 318 },
      { "stopNumber": 46, "stopName": "Gasnawa Watta Junction", "fare": 325 },
      { "stopNumber": 47, "stopName": "Nelumdeniya", "fare": 330 },
      { "stopNumber": 48, "stopName": "Bataponalla", "fare": 337 },
      { "stopNumber": 49, "stopName": "Siyambalapitiya", "fare": 343 },
      { "stopNumber": 50, "stopName": "Balalpatha Junction", "fare": 350 },
      { "stopNumber": 51, "stopName": "Galigamuwa", "fare": 355 },
      { "stopNumber": 52, "stopName": "Abanpitiya", "fare": 362 },
      { "stopNumber": 53, "stopName": "Ranwala Junction", "fare": 367 },
      { "stopNumber": 54, "stopName": "Kagalla", "fare": 374 },
      { "stopNumber": 55, "stopName": "Meepitiya", "fare": 381 },
      { "stopNumber": 56, "stopName": "Karandupotha", "fare": 386 },
      { "stopNumber": 57, "stopName": "Molgoda", "fare": 393 },
      { "stopNumber": 58, "stopName": "Mangalagama", "fare": 398 },
      { "stopNumber": 59, "stopName": "Ukuwakkanda", "fare": 405 },
      { "stopNumber": 60, "stopName": "Athwarama", "fare": 410 },
      { "stopNumber": 61, "stopName": "Mawanalla", "fare": 416 },
      { "stopNumber": 62, "stopName": "Beligammana", "fare": 421 },
      { "stopNumber": 63, "stopName": "Gingula", "fare": 428 },
      { "stopNumber": 64, "stopName": "Ganethanna", "fare": 433 },
      { "stopNumber": 65, "stopName": "Pahala Kadugannawa", "fare": 440 },
      { "stopNumber": 66, "stopName": "Kadugannawa", "fare": 445 },
      { "stopNumber": 67, "stopName": "Henawala", "fare": 452 },
      { "stopNumber": 68, "stopName": "Pilimathalawa", "fare": 457 },
      { "stopNumber": 69, "stopName": "Abilmigama", "fare": 464 },
      { "stopNumber": 70, "stopName": "Kiribathkubura", "fare": 470 },
      { "stopNumber": 71, "stopName": "Peradeniya", "fare": 477 },
      { "stopNumber": 72, "stopName": "Gatabe", "fare": 482 },
      { "stopNumber": 73, "stopName": "Mulgampola", "fare": 489 },
      { "stopNumber": 74, "stopName": "Kandy", "fare": 494 }
    ]
  }
};

// Upload to Firebase Realtime Database
async function uploadRouteData() {
  try {
    await db.ref("routes").set(routeData);
    console.log("✅ Route data uploaded successfully!");
  } catch (error) {
    console.error("❌ Error uploading data:", error);
  }
}

uploadRouteData();
