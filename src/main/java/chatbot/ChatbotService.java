package chatbot;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.cdimascio.dotenv.Dotenv;

@Service
//모델 인풋아웃풋 확인핋요
public class ChatbotService {

    private static final Dotenv env = Dotenv.load();
    private static final String HUGGINGFACE_API_KEY = env.get("HUGGINGFACE_API_KEY");
    public String SpeechToText(MultipartFile inputVoice){
        String outputText = (String)HFStringHttpResponse("openai/whisper-large-v3",inputVoice);
        return outputText;


    }
    public String emotionAnalysis(String inputChat){
        String emotion = (String)HFStringHttpResponse("KoGPT-emotion-analysis",inputChat);
        return emotion;
    }
    public String chatOutput(String inputChat, String emotion){
    
        String chatPrompt =
        """
            너는 한국어 텍스트에 최적화된 모델을 사용하고 있는 챗봇입니다. 너의 역할은 노인과 대화하며 그들의 감정을 이해하고, 
            적절한 응답을 제공하는 것입니다. 아래는 노인이 한 말과 그에 따른 감정입니다. 
            현재 노인의 말: %s 
            현재 노인의 감정: %s
            너는 이 정보를 바탕으로 노인의 감정을 고려하여 친절하고 공감이 가는 대답을 해야 합니다. 
            응답은 다음의 원칙을 따라야 합니다: 
            1. 감정에 맞는 공감과 위로를 제공할 것
            2. 긍정적인 기쁨,행복등의 감정은 함께 기뻐하며 축하해줄 것
            3. 부정적인 슬픔,외로움등의 감정은 공감하고 위로를 제공할 것
            4. 짧고 간결하면서도 따뜻하고 공손한 톤을 유지할 것
            5. 노인의 말에 대한 추가적인 질문을 하여 대화를 지속할 수 있도록 유도할 것
            너의 답변을 음성으로 전환할것이기 때문에 답변의의 형식은 특수문자등을 제외해서 오직 말로 표현가능한 형식만 사용해야합니다.

        """.formatted(inputChat,emotion);

        String outputText = (String)HFStringHttpResponse("beomi/kcbert-base",chatPrompt);
        return outputText;
    }
    public MultipartFile TextToSpeech(String outputText)
    {
        MultipartFile OutputSpeech = (MultipartFile)HFStringHttpResponse("coqui/XTTS-v2",outputText);
        return OutputSpeech;
    }
    //허깅페이스 문자열 응답 함수
    private Object HFStringHttpResponse(String models,Object input){

        WebClient webClient = WebClient.builder()
        .baseUrl("https://api-inference.huggingface.co/models")
        .defaultHeader("Authorization","Bearer"+ HUGGINGFACE_API_KEY)
        .build();

        
        if(input instanceof String)
        {
            input = (String)input;
        }
        else if(input instanceof MultipartFile)
        {

            input = (MultipartFile)input;
        }

        return webClient.post()
        .uri(models)
        .bodyValue(input)
        .retrieve()
        .bodyToMono(String.class)
        .block();

    }
}
