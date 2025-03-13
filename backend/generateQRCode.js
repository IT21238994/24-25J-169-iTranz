const QRCode = require('qrcode');

// Function to generate QR code as base64
const generateQRCode = async (data) => {
  try {
    const qrCodeDataUri = await QRCode.toDataURL(data);
    return qrCodeDataUri.split(',')[1]; // Return only the base64 part
  } catch (error) {
    throw new Error("Error generating QR code");
  }
};

module.exports = generateQRCode;
