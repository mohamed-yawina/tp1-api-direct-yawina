
package ma.emsi.yawina.tp1apidirectyawina.jsf;

import ma.emsi.yawina.tp1apidirectyawina.llm.JsonAdapterPourGemini;
import ma.emsi.yawina.tp1apidirectyawina.llm.LlmInteraction;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Backing bean pour la page JSF index.xhtml.
 * La portée ViewScoped conserve l'état pendant
 * plusieurs requêtes HTTP sur la même vue.
 */
@Named
@ViewScoped
public class Bb implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== ROLE SYSTEME =====

    private String roleSysteme;

    private boolean roleSystemeChangeable = true;

    private List<SelectItem> listeRolesSysteme;

    // ===== QUESTION ET REPONSE =====

    private String question;

    private String reponse;

    // ===== TP1 : MODE DEBUG =====

    private String texteRequeteJson;

    private String texteReponseJson;

    private boolean debug = false;

    // ===== HISTORIQUE =====

    private StringBuilder conversation = new StringBuilder();

    // ===== INJECTIONS CDI =====

    /**
     * Adapter permettant de construire les requêtes JSON,
     * communiquer avec Gemini et gérer son historique.
     */
    @Inject
    private JsonAdapterPourGemini jsonAdapter;

    /**
     * Contexte JSF pour afficher les messages d'erreur.
     */
    @Inject
    private FacesContext facesContext;

    // ===== CONSTRUCTEUR =====

    public Bb() {
    }

    // ===== GETTERS ET SETTERS =====

    public String getRoleSysteme() {
        return this.roleSysteme;
    }

    public void setRoleSysteme(String roleSysteme) {
        this.roleSysteme = roleSysteme;
    }

    public boolean isRoleSystemeChangeable() {
        return this.roleSystemeChangeable;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return this.reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    // ===== GETTERS ET SETTERS DEBUG =====

    public String getTexteRequeteJson() {
        return this.texteRequeteJson;
    }

    public void setTexteRequeteJson(String texteRequeteJson) {
        this.texteRequeteJson = texteRequeteJson;
    }

    public String getTexteReponseJson() {
        return this.texteReponseJson;
    }

    public void setTexteReponseJson(String texteReponseJson) {
        this.texteReponseJson = texteReponseJson;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    // ===== CONVERSATION =====

    public String getConversation() {
        return this.conversation.toString();
    }

    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(conversation);
    }

    /**
     * Envoie une question à Gemini.
     *
     * @return null pour rester sur la même page JSF.
     */
    public String envoyer() {

        // ===== DIAGNOSTIC : AVANT ENVOI =====

        System.out.println("===== DIAGNOSTIC TP1 =====");
        System.out.println(
                "Instance Bb : " + System.identityHashCode(this)
        );
        System.out.println(
                "Instance JsonAdapter : "
                        + System.identityHashCode(jsonAdapter)
        );
        System.out.println(
                "Conversation avant envoi : " + conversation
        );
        System.out.println(
                "Question : " + question
        );

        // ===== VERIFICATION QUESTION =====

        if (this.question == null || this.question.isBlank()) {

            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Texte question vide",
                    "Il manque le texte de la question"
            );

            facesContext.addMessage(null, message);

            return null;
        }

        // ===== DEBUT DE CONVERSATION =====

        if (this.conversation.isEmpty()) {

            // Bloque le changement du rôle système.
            this.roleSystemeChangeable = false;

            // Configure le rôle système dans l'adapter.
            jsonAdapter.setSystemRole(this.roleSysteme);

            System.out.println(
                    "Nouvelle conversation : rôle système initialisé"
            );

        } else {

            System.out.println(
                    "Conversation existante : conservation du contexte"
            );
        }

        // ===== APPEL A L'API GEMINI =====

        try {

            LlmInteraction interaction =
                    jsonAdapter.envoyerRequete(this.question);

            // Réponse textuelle du LLM
            this.reponse = interaction.reponseExtraite();

            // JSON envoyé à Gemini
            this.texteRequeteJson = interaction.questionJson();

            // JSON reçu de Gemini
            this.texteReponseJson = interaction.reponseJson();

            System.out.println(
                    "Réponse Gemini reçue avec succès"
            );

        } catch (Exception e) {

            this.reponse = "Erreur : " + e.getMessage();

            System.err.println(
                    "Erreur pendant l'appel Gemini : "
                            + e.getMessage()
            );

            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Problème de connexion avec l'API du LLM",
                    "Problème de connexion avec l'API du LLM"
                            + e.getMessage()
                            + "\n"
                            + Arrays.toString(e.getStackTrace())
            );

            facesContext.addMessage(null, message);
        }

        // ===== MISE A JOUR DE LA CONVERSATION =====

        afficherConversation();

        // ===== DIAGNOSTIC : APRES ENVOI =====

        System.out.println(
                "Conversation après envoi : " + conversation
        );

        System.out.println(
                "===== FIN DIAGNOSTIC TP1 ====="
        );

        return null;
    }

    /**
     * Démarre un nouveau chat.
     */
    public String nouveauChat() {
        return "index";
    }

    /**
     * Affiche l'historique des questions et réponses.
     */
    private void afficherConversation() {

        this.conversation
                .append("== User:\n")
                .append(this.question)
                .append("\n== Serveur:\n")
                .append(this.reponse)
                .append("\n");
    }

    /**
     * Liste des rôles système prédéfinis.
     */
    public List<SelectItem> getRolesSysteme() {

        if (this.listeRolesSysteme == null) {

            this.listeRolesSysteme = new ArrayList<>();

            // ===== ASSISTANT =====

            String role = """
                    You are a helpful assistant. You help the user to find the information they need.
                    If the user type a question, you answer it.
                    """;

            this.listeRolesSysteme.add(
                    new SelectItem(role, "Assistant")
            );

            // ===== TRADUCTEUR =====

            role = """
                    You are an interpreter. You translate from English to French and from French to English.
                    If the user type a French text, you translate it into English.
                    If the user type an English text, you translate it into French.
                    If the text contains only one to three words, give some examples of usage of these words in English.
                    """;

            this.listeRolesSysteme.add(
                    new SelectItem(role, "Traducteur Anglais-Français")
            );

            // ===== GUIDE TOURISTIQUE =====

            role = """
                    Your are a travel guide. If the user type the name of a country or of a town,
                    you tell them what are the main places to visit in the country or the town
                    are you tell them the average price of a meal.
                    """;

            this.listeRolesSysteme.add(
                    new SelectItem(role, "Guide touristique")
            );
        }

        return this.listeRolesSysteme;
    }
}
