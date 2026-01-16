require("dotenv").config();

const config = {
  server: process.env.DB_SERVER,
  database: process.env.DB_NAME,
  options: {
    trustServerCertificate: true, // Dev local cần cái này
  },
};

module.exports = config;
