
package ma.emsi.yawina.tp1apidirectyawina.llm;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;

/**
 * Gère l'interface avec l'API de Gemini.
 * Son rôle est essentiellement de lancer une requête à chaque nouvelle
 * question qu'on veut envoyer à l'API.
 *
 * De portée dependent pour réinitialiser la conversation à chaque fois que
 * l'instance qui l'utilise est renouvelée.
 * Par exemple, si l'instance qui l'utilise est de portée View, la conversation est
 * réinitialisée à chaque fois que l'utilisateur quitte la page en cours.
 */
@Dependent
public class LlmClientPourGemini implements Serializable {

    // Clé pour l'API du LLM
    private final String key;

    // Client REST. Facilite les échanges avec une API REST.
    private Client clientRest; // Pour pouvoir le fermer

    // Représente un endpoint de serveur REST
    private final WebTarget target;

    // Remarque : éventuel problème si Client et WebTarget ne sont pas Serializable
    // et si une passivation doit avoir lieu.
    // Mais c'est une autre histoire, liée à Jakarta et pas à l'IA...

    public LlmClientPourGemini() {
        // Récupère la clé secrète pour travailler avec l'API du LLM,
        // mise dans une variable d'environnement du système d'exploitation.
        this.key = System.getenv("GEMINI_KEY");

        if (this.key == null || this.key.isBlank()) {
            throw new IllegalStateException(
                    "La variable d'environnement GEMINI_KEY est absente ou vide."
            );
        }

        // Client REST pour envoyer des requêtes vers les endpoints de l'API du LLM
        this.clientRest = ClientBuilder.newClient();

        // Endpoint REST pour envoyer la question à l'API.
        this.target = clientRest.target("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent")
                .queryParam("key", this.key);
    }

    /**
     * Envoie une requête à l'API de Gemini.
     *
     * @param requestEntity le corps de la requête (en JSON).
     * @return réponse REST de l'API (corps en JSON).
     */
    public Response envoyerRequete(Entity requestEntity) {
        Invocation.Builder request = target.request(MediaType.APPLICATION_JSON_TYPE);

        // Envoie la requête POST au LLM
        return request.post(requestEntity);
    }

    @PreDestroy
    public void closeClient() {
        if (this.clientRest != null) {
            this.clientRest.close();
        }
    }
}
