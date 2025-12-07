package finanzas.web.rest.dto;

public class WhatsappInboundMessageDTO {

    private String from; // Número de teléfono del remitente
    private String body; // Cuerpo del mensaje

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "WhatsappInboundMessageDTO{\"from=\"" + from + "'\", body='" + body + "'}";
    }
}