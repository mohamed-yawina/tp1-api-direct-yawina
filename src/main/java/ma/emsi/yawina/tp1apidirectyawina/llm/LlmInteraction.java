
package ma.emsi.yawina.tp1apidirectyawina.llm;

/**
 * Représente une interaction avec l'API du LLM.
 *
 * @param questionJson JSON envoyé à Gemini
 * @param reponseJson JSON reçu de Gemini
 * @param reponseExtraite réponse textuelle extraite du JSON
 */
public record LlmInteraction(
        String questionJson,
        String reponseJson,
        String reponseExtraite
) {
}
