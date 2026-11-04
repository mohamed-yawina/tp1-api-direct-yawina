
package ma.emsi.yawina.tp1apidirectyawina.service;

import jakarta.enterprise.context.Dependent;

import java.io.Serializable;
import java.util.Locale;

/**
 * Service de génération et de modification des réponses.
 *
 * TP 0 - Jakarta EE
 *
 * Le service conserve le traitement initial :
 * - Affichage du rôle système en majuscules lors de la première réponse.
 * - Transformation de la question en minuscules.
 * - Encadrement du résultat avec "||".
 *
 * Amélioration personnelle :
 * - Analyse du nombre de mots.
 * - Analyse du nombre de caractères.
 * - Détection simple du type de question.
 */
@Dependent
public class GenerateurReponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Indique si le rôle système a déjà été affiché.
     */
    private boolean roleSystemeAffiche = false;

    /**
     * Génère une réponse à partir de la question de l'utilisateur.
     *
     * @param question   Question saisie par l'utilisateur.
     * @param roleSysteme Rôle système sélectionné.
     * @return Réponse générée avec analyse de la question.
     */
    public String genererReponse(String question, String roleSysteme) {

        // Protection contre une éventuelle valeur null.
        if (question == null || question.isBlank()) {
            return "Erreur : la question ne peut pas être vide.";
        }

        StringBuilder resultat = new StringBuilder("||");

        // 1. Traitement initial : affichage du rôle système une seule fois.
        if (!this.roleSystemeAffiche && roleSysteme != null) {
            resultat.append(roleSysteme.toUpperCase(Locale.FRENCH))
                    .append("\n");

            this.roleSystemeAffiche = true;
        }

        // 2. Traitement initial : conversion en minuscules.
        resultat.append(question.toLowerCase(Locale.FRENCH))
                .append("||");

        // 3. Amélioration personnelle : analyse de la question.
        resultat.append(analyserQuestion(question));

        return resultat.toString();
    }

    /**
     * Analyse la question saisie par l'utilisateur.
     *
     * Cette méthode constitue le traitement personnel
     * ajouté dans le cadre du bonus du TP 0.
     *
     * @param question Question à analyser.
     * @return Informations sur le texte.
     */
    private String analyserQuestion(String question) {

        String texte = question.trim();

        // Calcul du nombre de mots.
        int nombreMots = texte.isEmpty()
                ? 0
                : texte.split("\\s+").length;

        // Calcul du nombre de caractères, espaces internes compris.
        int nombreCaracteres = texte.length();

        // Détection simple du type de question.
        String typeQuestion;

        if (texte.endsWith("?")) {
            typeQuestion = "Interrogative";
        } else {
            typeQuestion = "Déclarative";
        }

        // Construction de l'analyse.
        StringBuilder analyse = new StringBuilder();

        analyse.append("\n\n");
        analyse.append("===== ANALYSE DE LA QUESTION =====\n");
        analyse.append("Nombre de mots : ")
                .append(nombreMots)
                .append("\n");

        analyse.append("Nombre de caractères : ")
                .append(nombreCaracteres)
                .append("\n");

        analyse.append("Type de question : ")
                .append(typeQuestion)
                .append("\n");

        analyse.append("==================================");

        return analyse.toString();
    }
}
