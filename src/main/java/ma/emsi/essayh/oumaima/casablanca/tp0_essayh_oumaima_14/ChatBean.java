package ma.emsi.essayh.oumaima.casablanca.tp0_essayh_oumaima_14;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.*;

@Named("chatBeanPrimary")
@ViewScoped
public class ChatBean implements Serializable {

    private String systemRole;
    private boolean systemRoleChangeable = true;
    private String question;
    private String reponse;
    private StringBuilder conversation = new StringBuilder();

    @Inject
    private FacesContext facesContext;

    public ChatBean() {
    }

    public String getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
    }

    public boolean isSystemRoleChangeable() {
        return systemRoleChangeable;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getConversation() {
        return conversation.toString();
    }

    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(conversation);
    }

    public String getConseilAleatoire() {
        List<String> conseils = new ArrayList<>();
        conseils.add("N'oubliez pas de prendre une pause pendant votre journée de travail.");
        conseils.add("Mangez équilibré et faites de l'exercice régulièrement.");
        conseils.add("Lisez au moins 15 minutes par jour pour élargir vos horizons.");
        conseils.add("Ne laissez pas les petites frustrations ruiner votre journée.");
        conseils.add("Prenez le temps d'apprécier les petites choses dans la vie.");

        Random rand = new Random();
        return conseils.get(rand.nextInt(conseils.size()));
    }

    public String envoyer() {
        if (question == null || question.isBlank()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Texte question vide", "Il manque le texte de la question");
            facesContext.addMessage(null, message);
            return null;
        }

        System.out.println("=== Début du traitement ===");
        System.out.println("Rôle sélectionné : " + systemRole);
        System.out.println("Question reçue : " + question);

        if (systemRole == null || systemRole.isBlank()) {
            this.reponse = "Aucun rôle n'a été sélectionné. Veuillez choisir un rôle.";
            afficherConversation();
            return null;
        }

        // Détection du rôle et appel au traitement correspondant
        if (systemRole.contains("Assistant")) {
            traiterAssistant();
        } else if (systemRole.contains("Traducteur Anglais-Français")) {
            traiterTraducteur();
        } else if (systemRole.contains("Guide touristique")) {
            traiterGuideTouristique();
        } else {
            this.reponse = getConseilAleatoire();
        }

        if (this.conversation.isEmpty()) {
            this.reponse = systemRole.toUpperCase(Locale.FRENCH) + "\n" + this.reponse;
            this.systemRoleChangeable = false;
        }

        afficherConversation();
        System.out.println("=== Fin du traitement ===");
        return null;
    }

    private void traiterGuideTouristique() {
        this.reponse = "Je suis un guide touristique virtuel ! Posez-moi des questions sur des lieux ou des voyages.";
    }

    private void traiterTraducteur() {
        this.reponse = traduireAnglaisFrancais(question);
    }

    private void traiterAssistant() {
        String questionLower = question.toLowerCase(Locale.FRENCH);
        if (questionLower.contains("what time is it") || questionLower.contains("quelle heure est-il")) {
            this.reponse = obtenirHeureActuelle();
        } else if (questionLower.contains("how are you") || questionLower.contains("comment ça va")) {
            this.reponse = "Je suis une machine, mais merci de demander !";
        } else {
            this.reponse = "Je ne sais pas répondre à cette question. Essayez autre chose.";
        }
    }

    public String obtenirHeureActuelle() {
        LocalTime time = LocalTime.now();
        return "L'heure actuelle est : " + time.getHour() + "h" + time.getMinute();
    }

    public String traduireAnglaisFrancais(String texte) {
        Map<String, String> dictionnaire = new HashMap<>();
        dictionnaire.put("hello", "bonjour");
        dictionnaire.put("how are you", "comment ça va");
        dictionnaire.put("what time is it", "quelle heure est-il");
        dictionnaire.put("goodbye", "au revoir");

        String texteLower = texte.toLowerCase(Locale.FRENCH).trim();
        return dictionnaire.getOrDefault(texteLower,
                "Je ne connais pas la traduction pour : " + texte + ". Essayez des mots simples !");
    }

    public String nouveauChat() {
        return "index";
    }

    private void afficherConversation() {
        this.conversation.append("== User:\n").append(question).append("\n== Serveur:\n").append(reponse).append("\n");
    }

    public List<SelectItem> getSystemRoles() {
        List<SelectItem> listeSystemRoles = new ArrayList<>();
        String role = "Assistant";
        listeSystemRoles.add(new SelectItem(role, "Assistant"));

        role = "Traducteur Anglais-Français";
        listeSystemRoles.add(new SelectItem(role, "Traducteur Anglais-Français"));

        role = "Guide touristique";
        listeSystemRoles.add(new SelectItem(role, "Guide touristique"));

        this.systemRole = listeSystemRoles.get(0).getValue().toString();
        return listeSystemRoles;
    }
}
