#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

// Define OLED display width and height
#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64

// Define the I2C address for the OLED
#define OLED_IIC_ADDRESS 0x3C // Most SSD1306 OLED displays use this address

// Declare an OLED display object
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, -1); // -1 if reset pin is not used

void setup() {
  // Initialize Serial Monitor
  Serial.begin(115200);

  // Start the OLED display
  if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3D)) { // Use only the address
    Serial.println(F("SSD1306 allocation failed"));
    for (;;); // Don't proceed, loop forever
  }

  // Clear the display buffer
  display.clearDisplay();

  // Set text size and color
  display.setTextSize(2); // Text size (1 is the smallest)
  display.setTextColor(SSD1306_WHITE); // White text

  // Display "Hello, World!" text
  display.setCursor(10, 20); // x=10, y=20
  display.println("Hello,");
  display.setCursor(10, 40);
  display.println("World!");

  // Send buffer to the display
  display.display();
}

void loop() {
  // Do nothing in the loop
}
