const QRCode = require('qrcode');

exports.generateQRCode = async (data) => {
  try {
    const qrImage = await QRCode.toDataURL(data);
    return qrImage;
  } catch (error) {
    throw new Error('QR code generation failed.');
  }
};