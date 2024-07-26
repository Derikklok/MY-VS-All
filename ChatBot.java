import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class ChatBot {
    private static List<QA> knowledgeBase;

    public ChatBot() {
        loadKnowledgeBase("C:\\Users\\User\\Desktop\\Coding\\JAVA\\AI\\ChatBot-V1.0\\src\\main\\java\\knowledge_base.json");
    }

    private void loadKnowledgeBase(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type qaListType = new TypeToken<List<QA>>(){}.getType();
            knowledgeBase = gson.fromJson(reader, qaListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getResponse(String userQuery) {
        Optional<QA> match = knowledgeBase.stream()
                .filter(qa -> qa.getQuestion().equalsIgnoreCase(userQuery))
                .findFirst();

        return match.map(QA::getAnswer).orElse("I don't know the answer to that question.");
    }

    private static class QA {
        private String question;
        private String answer;

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }
    }
}
