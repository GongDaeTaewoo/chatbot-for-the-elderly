package chatbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ChatbotController {
    private final ChatbotService chatbotService;
    private String inputText;
    private String emotion;
    private String outputText;
    private MultipartFile outputVoice;

    @Autowired
    public ChatbotController(ChatbotService chatbotService){
        this.chatbotService=chatbotService;
    }

    @PostMapping("/chat")
    public ResponseEntity<MultipartFile> chat(@RequestParam MultipartFile inputVoice){
        inputText = chatbotService.SpeechToText(inputVoice);
        emotion = chatbotService.emotionAnalysis(inputText);
        outputText = chatbotService.chatOutput(inputText,emotion);
        outputVoice =chatbotService.TextToSpeech(outputText);
        return new ResponseEntity<>(outputVoice, HttpStatus.OK);
    }

}
