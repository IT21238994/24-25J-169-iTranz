const express = require('express');
const app = express();
const cors = require('cors');
const bodyParser = require('body-parser');

const driverRoutes = require('./routes/driverRoutes');
const passengerRoutes = require('./routes/passengerRoutes');
const paymentRoutes = require('./routes/paymentRoutes');
const uploadRoutes = require('./routes/uploadRoutes');

app.use(cors());
app.use(bodyParser.json());

app.use('/api/driver', driverRoutes);
app.use('/api/passenger', passengerRoutes);
app.use('/api/payment', paymentRoutes);
app.use('/api/upload', uploadRoutes);

const PORT = 5000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
