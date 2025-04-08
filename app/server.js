const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const { Pool } = require('pg');

const app = express();
const port = 3000;

// Middleware
app.use(cors());
app.use(bodyParser.json());

// PostgreSQL connection pool
const pool = new Pool({
  user: 'postgres',
  host: 'localhost', // or your IP if database is remote
  database: 'test',
  password: 'amayno99',
  port: 5432,
});

// Route to add a cultivateur
app.post('/addCultivateur', async (req, res) => {
  const { nom, prenom, tel, email, region, ville, zone, password } = req.body;
  try {
    const result = await pool.query(
      'INSERT INTO "cultivateurId" (nom, prenom, tel, email, region, ville, zone, password) VALUES ($1, $2, $3, $4, $5, $6, $7, $8) RETURNING user_id',
      [nom, prenom, tel, email, region, ville, zone, password]
    );

    res.json({
      success: true,
      userId: result.rows[0].user_id,
    });
  } catch (err) {
    console.error('Database error:', err);
    res.status(500).json({ success: false, message: 'Failed to add cultivateur' });
  }
});

// Start the server (on all interfaces)
app.listen(port, '0.0.0.0', () => {
  console.log(`🚀 Server running on http://0.0.0.0:${port}`);
});
