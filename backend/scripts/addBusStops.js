const { db } = require("../config/firebaseConfig");

const busStopsData = {
  "panadura_kandy": {
    "0": { name: "Panadura", fare: 0 },
    "1": { name: "Aluth Palama", fare: 27 },
    "2": { name: "Egodauyana", fare: 35 },
    "3": { name: "Koralawalla", fare: 45 },
    "4": { name: "Moratuwa", fare: 56 },
    "5": { name: "Katubadda", fare: 66 },
    "6": { name: "Ratmalana", fare: 77 },
    "7": { name: "Galkissa", fare: 87 },
    "8": { name: "Dehiwala", fare: 90 },
    "9": { name: "Miris Mola", fare: 97 },
    "10": { name: "Kalubowila", fare: 104 },
    "11": { name: "Nugegoda", fare: 111 },
    "12": { name: "Pitakotte", fare: 117 },
    "13": { name: "Athulkotte", fare: 123 },
    "14": { name: "Battaramulla", fare: 130 },
    "15": { name: "Koswatta Junction", fare: 136 },
    "16": { name: "Malabe", fare: 141 },
    "17": { name: "Pittugala", fare: 148 },
    "18": { name: "Kothalawala", fare: 153 },
    "19": { name: "Kaduwela", fare: 160 },
    "20": { name: "Malwana", fare: 165 },
    "21": { name: "Biyagama", fare: 172 },
    "22": { name: "Siyabalape", fare: 177 },
    "23": { name: "Delgoda", fare: 184 },
    "24": { name: "Naranwila", fare: 190 },
    "25": { name: "Ambarumluwa Temple", fare: 197 },
    "26": { name: "Weliweriya", fare: 202 },
    "27": { name: "Rathupaswala", fare: 209 },
    "28": { name: "Miriswatta", fare: 214 },
    "29": { name: "Yakkala", fare: 221 },
    "30": { name: "Aluthgama", fare: 228 },
    "31": { name: "Kalagedihena", fare: 233 },
    "32": { name: "Thihariya", fare: 240 },
    "33": { name: "Nittambuwa Sangha Maha Vidyalaya", fare: 245 },
    "34": { name: "Nittambuwa", fare: 252 },
    "35": { name: "Kalalpitiya", fare: 257 },
    "36": { name: "Pasyala", fare: 264 },
    "37": { name: "Kajugama", fare: 270 },
    "38": { name: "Radawadunna", fare: 277 },
    "39": { name: "Wewaldeniya", fare: 282 },
    "40": { name: "Danowita", fare: 289 },
    "41": { name: "Dummaladeniya", fare: 294 },
    "42": { name: "Warakapola", fare: 301 },
    "43": { name: "Ambepussa", fare: 306 },
    "44": { name: "Maahena", fare: 313 },
    "45": { name: "Tholangamuwa", fare:318 },
    "46": { name: "Gasnawa Watta Junction", fare: 325 },
    "47": { name: "Nelumdeniya", fare: 330 },
    "48": { name: "Bataponalla", fare: 337 },
    "49": { name: "Siyambalapitiya", fare: 343 },
    "50": { name: "Balalpatha Junction", fare: 350 },
    "51": { name: "Galigamuwa", fare: 355 },
    "52": { name: "Abanpitiya", fare: 362 },
    "53": { name: "Ranwala Junction", fare: 367 },
    "54": { name: "Kagalla", fare: 374 },
    "55": { name: "Meepitiya", fare: 381 },
    "56": { name: "Karandupotha", fare: 386 },
    "57": { name: "Molgoda", fare: 393 },
    "58": { name: "Mangalagama", fare: 398 },
    "59": { name: "Ukuwakkanda", fare: 405 },
    "60": { name: "Athwarama", fare: 410 },
    "61": { name: "Mawanalla", fare: 416 },
    "62": { name: "Beligammana", fare: 421 },
    "63": { name: "Gingula", fare: 428 },
    "64": { name: "Ganethanna", fare: 433 },
    "65": { name: "Pahala Kadugannawa", fare: 440 },
    "66": { name: "Kadugannawa", fare: 445 },
    "67": { name: "Henawala", fare: 452 },
    "68": { name: "Pilimathalawa", fare: 457 },
    "69": { name: "Abilmigama", fare: 464 },
    "70": { name: "Kiribathkubura", fare: 470 },
    "71": { name: "Peradeniya", fare: 477 },
    "72": { name: "Gatabe", fare: 4820 },
    "73": { name: "Mulgampola", fare: 489 },
    "74": { name: "Kandy", fare: 494 }
  },


  "panadura_colombo": {
    "1": { name: "Panadura", fare: 120 },
    "2": { name: "Kalutara", fare: 180 }
  }
};

// Reference to the busStops node in Firebase
const busStopsRef = db.ref("busStops");

// Set bus stop data in Firebase
busStopsRef.set(busStopsData)
  .then(() => {
    console.log("Bus stops data added successfully!");
  })
  .catch((error) => {
    console.error("Error adding bus stops data: ", error);
  });
