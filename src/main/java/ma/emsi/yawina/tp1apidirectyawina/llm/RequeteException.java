
package ma.emsi.yawina.tp1apidirectyawina.llm;

/**
 * Exception levée lorsqu'une requête à l'API du LLM échoue.
 */
public class RequeteException extends Exception {

    private final String requeteJson;

    public RequeteException(String message, String requeteJson) {
        super(message);
        this.requeteJson = requeteJson;
    }

    public String getRequeteJson() {
        return requeteJson;
    }
}
