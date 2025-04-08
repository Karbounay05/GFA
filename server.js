const express = require('express');
const { Client } = require('pg');
const app = express();
const port = 3000;

// Configurer la connexion à PostgreSQL
const client = new Client({
  user: 'postgres',
  host: 'localhost',
  database: 'GFA',
  password: 'ali2004',
  port: 5432,
});

client.connect();
app.use(express.json());
// Route pour obtenir les utilisateurs
app.get('/users', (req, res) => {
  client.query('SELECT * FROM "User"', (err, result) => {
    if (err) {
      res.status(500).send('Erreur de base de données');
    } else {
      res.json(result.rows);  // Renvoie les utilisateurs en JSON
    }
  }); 
});
app.post('/register', async (req, res) => {
  const { nom, prenom, tel , email, motdepasse,region, ville, zone } = req.body;

  try {
    const query = 'INSERT INTO "User" (nom, prenom,numero, email, mot_de_passe,region, ville, zone) VALUES($1, $2, $3, $4, $5, $6, $7, $8)';
    const values = [nom, prenom, tel,email,motdepasse, region, ville, zone];
    
    await client.query(query, values);
    
    res.status(200).send('Utilisateur enregistré avec succès');
  } catch (err) {
    console.error(err);
    res.status(500).send('Erreur lors de l\'enregistrement');
  }
});
app.listen(3000, '0.0.0.0', () => {
  console.log("Serveur Node.js en écoute sur le port 3000");
});