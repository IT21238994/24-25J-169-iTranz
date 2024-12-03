// #include <Arduino.h>

// // put function declarations here:
// int myFunction(int, int);

// void setup() {
//   // put your setup code here, to run once:
//   int result = myFunction(2, 3);
// }

// void loop() {
//   // put your main code here, to run repeatedly:
// }

// // put function definitions here:
// int myFunction(int x, int y) {
//   return x + y;
// }


#include <Wire.h>
#include <Adafruit_SSD1306.h>
#include <TinyGPS++.h>
#include <HardwareSerial.h>
HardwareSerial mySerial(1); // Use UART1

//#include <SoftwareSerial.h>

// Define OLED display dimensions
#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64

Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, -1);

// GPS and GSM
TinyGPSPlus gps;
SoftwareSerial gpsSerial(16, 17); // RX, TX for GPS
SoftwareSerial gsmSerial(1, 3);  // RX, TX for GSM

void setup() {
  // Initialize Serial Monitor
  Serial.begin(115200);

  // Initialize GPS
  gpsSerial.begin(9600);

  // Initialize GSM
  gsmSerial.begin(9600);

  // Initialize OLED
  if (!display.begin(SSD1306_I2C_ADDRESS, 0x3C)) {
    Serial.println(F("OLED initialization failed!"));
    while (1);
  }
  display.clearDisplay();
}

void loop() {
  // GPS Data
  while (gpsSerial.available() > 0) {
    gps.encode(gpsSerial.read());
    if (gps.location.isUpdated()) {
      Serial.print("Latitude: ");
      Serial.println(gps.location.lat(), 6);
      Serial.print("Longitude: ");
      Serial.println(gps.location.lng(), 6);
    }
  }

  // Display on OLED
  display.clearDisplay();
  display.setCursor(0, 0);
  display.print("GPS Data:");
  display.print("Lat: "); display.println(gps.location.lat(), 6);
  display.print("Lon: "); display.println(gps.location.lng(), 6);
  display.display();

  // Brake Wear Sensor
  int brakeValue = analogRead(34); // Adjust based on pin
  Serial.print("Brake Wear Value: ");
  Serial.println(brakeValue);

  delay(1000);
}
