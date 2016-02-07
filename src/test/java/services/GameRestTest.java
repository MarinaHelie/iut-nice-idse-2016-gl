package services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import fr.unice.idse.constante.Config;
import fr.unice.idse.model.Game;
import fr.unice.idse.model.Model;
import fr.unice.idse.services.GameRest;



public class GameRestTest extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(GameRest.class);
	}

	Model model;
	Game game;

	@Before
	public void init() {
		model = Model.getInstance();
		model.setGames(new ArrayList<Game>());
		model.addGame(model.createPlayer("toto"), "tata", 4);
		game = model.findGameByName("tata");
	}

	@Test
	public void retourneLeJoueurActuelDeLaPartie() throws JSONException {
		// Init the game
		game.addPlayer(model.createPlayer("marcel"));
		game.addPlayer(model.createPlayer("chris"));
		game.addPlayer(model.createPlayer("maurice"));
		game.start();

		// Test the methods
		Response response = target("/game/tata/command").request().get();
		JSONObject json = new JSONObject(response.readEntity(String.class));

		// Assert
		assertEquals(200, response.getStatus());
		assertEquals("toto", json.getString("pseudo"));
	}

	@Test
	public void retourneErreur401SiLaPartieEstPasDemarre() throws JSONException {
		// Test the methods
		Response response = target("/game/tata/command").request().get();
		JSONObject json = new JSONObject(response.readEntity(String.class));

		// Assert
		assertEquals(401, response.getStatus());
		assertEquals("Game has not begin", json.getString("error"));
	}

	@Test
    public void retourneFalseSiLaPartieExisteMaisPasCommencer() throws JSONException{
        Response response = target("/game/tata").request().get();
        assertEquals(200, response.getStatus());
        JSONObject json = new JSONObject(response.readEntity(String.class));
        assertEquals(false, json.getBoolean("state"));
    }

    @Test
    public void retourneUneErreur404SiPartieNexistePas() {
        Response response = target("/game/sdsdsdss").request().get();
        assertEquals(404, response.getStatus());
    }

    @Test
    public void ajouteUnJoueurInexistantDansUnePartie() throws JSONException{
        String json = "{_token: '"+ Config._token+"', pseudo: 'titi'}";
        Entity<String> jsonEntity = Entity.entity(json, MediaType.APPLICATION_JSON);
        Response response = target("/game/tata").request().put(jsonEntity);

        assertEquals(200, response.getStatus());
        JSONObject jsonresponse = new JSONObject(response.readEntity(String.class));
        assertTrue(jsonresponse.getBoolean("status"));
    }

    @Test
    public void ajouterUnJoueurExistantDansUnePartie() throws JSONException{
        String json = "{pseudo: 'toto'}";
        Entity<String> jsonEntity = Entity.entity(json, MediaType.APPLICATION_JSON);
        Response response = target("/game/tata/addplayer").request().post(jsonEntity);

        assertEquals(405, response.getStatus());
    }

    @Test
    public void getHandDunJoueur() throws JSONException{
        for(int i = 0; i < 3; i++)
            model.addPlayerToGame("tata", model.createPlayer("azert"+i));

        model.findGameByName("tata").start();
        Response response = target("/game/tata/toto").request().get();
        assertEquals(200, response.getStatus());
        JSONObject jsonresponse = new JSONObject(response.readEntity(String.class));
        assertEquals(7, jsonresponse.getJSONArray("cartes").length());
    } 
    

    @Test
    public void lancerUnePartieSansTousLesJoueurs() throws JSONException{
        String json = "{pseudo: 'toto'}";
        Entity<String> jsonEntity = Entity.entity(json, MediaType.APPLICATION_JSON);
        Response response = target("/game/tata/command").request().put(jsonEntity);

        assertEquals(500, response.getStatus());
        assertEquals("Game not tucked", response.readEntity(String.class));
    }

    @Test
    public void lancerUnePartieQuiADejaCommencer() throws JSONException{
        for(int i = 0; i < 3; i++)
            model.addPlayerToGame("tata", model.createPlayer("azert"+i));
        model.findGameByName("tata").start();

        String json = "{pseudo: 'toto'}";
        Entity<String> jsonEntity = Entity.entity(json, MediaType.APPLICATION_JSON);
        Response response = target("/game/tata/command").request().put(jsonEntity);

        assertEquals(500, response.getStatus());
        assertEquals("Game started", response.readEntity(String.class));
    }

    @Test
    public void lancerUnePartieAvecTousLesJoueurs() throws JSONException{
        for(int i = 0; i < 3; i++)
            model.addPlayerToGame("tata", model.createPlayer("azert"+i));

        String json = "{pseudo: 'toto'}";
        Entity<String> jsonEntity = Entity.entity(json, MediaType.APPLICATION_JSON);
        Response response = target("/game/tata/command").request().put(jsonEntity);

        assertEquals(200, response.getStatus());
        JSONObject jsonresponse = new JSONObject(response.readEntity(String.class));
        assertTrue(jsonresponse.getBoolean("status"));
    }

    @Test
    public void createTest(){
        /**
         * Creation dun tableau formaté JSON avec les 3 parametres
         */
        String json = "{_token: 'hbj7BB7Y6B87T282B87T27N90A098', game: 'superfly', player: 'marcel'}";
        Entity<String> jsonEntity = Entity.entity(json, MediaType.APPLICATION_JSON);

        /**
         * on verifie que le code de retour est bien 200/succes
         */
        Response response = target("/game").request().post(jsonEntity);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void retourneStatus200PourAfficherToutesLesGames(){
        Response response = target("/game").request().get();
        assertEquals(200, response.getStatus());
    }

    /**
     * Doit retourner un tableau vide vu qu'aucune partie n'a été créé
     * @throws JSONException
     */
    @Test
    public void retourneUnTableauVideSiAucuneGame() throws JSONException{
        model.setGames(new ArrayList<>());
        Response response = target("/game").request().get();
        assertEquals(200, response.getStatus());

        // Parse la reponse en JSON
        JSONObject json = new JSONObject(response.readEntity(String.class));
        assertEquals(0, json.getJSONArray("games").length());
    }

    @Test
    public void retourneUnTableauAvecUneGame() throws JSONException{
        Response response = target("/game").request().get();
        assertEquals(200, response.getStatus());

        // Parse la reponse en JSON
        JSONObject json = new JSONObject(response.readEntity(String.class));
        assertEquals(1, json.getJSONArray("games").length());
    }
    
    @Test
    public void pickacardTest() throws JSONException{
    	model.findGameByName("tata").start();
    	String json = "{game:'tata', pseudo: 'toto'}";
        Entity<String> jsonEntity = Entity.entity(json, MediaType.APPLICATION_JSON);
        Response response = target("/game/tata/toto").request().post(jsonEntity);
    	assertEquals(200, response.getStatus());
    	

    }

}
