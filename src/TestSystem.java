import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 测试系统
 * @author 朱洪椿
 */
public class TestSystem
{
    public static TestSystem instance;
    public static final String filePath = "resources/questions.csv";
    public int timeSeconds = 120;
    public int testNumber = 10;
    private final List<Question> questionList;

    public TestSystem()
    {
        questionList = new ArrayList<>();
        instance = this;
    }

    /**
     * 从文件导入题库信息
     */
    private void ImportQuestions()
    {
        questionList.clear();
        File file = new File(filePath);
        try{
            BufferedReader textFile = new BufferedReader(new FileReader(file));
            String lineData;

            while ((lineData = textFile.readLine()) != null)
            {
                String[] values = lineData.split(",");
                if (values.length != 5 || Objects.equals(values[0], "序号")) continue;
                int index = Integer.parseInt(values[0]);
                QuestionType type;
                switch (values[2])
                {
                    case "选择" -> type = QuestionType.choice;
                    case "填空" -> type = QuestionType.fixing;
                    case "判断" -> type = QuestionType.judge;
                    default -> {
                        System.out.println("题目类型有误：" + values[2]);
                        continue;
                    }
                }
                List<String> choices = Arrays.stream(values[3].split(";")).toList();
                Question newQuestion = new Question(index, values[1], type, choices, values[4]);
                questionList.add(newQuestion);
            }
            textFile.close();
        }catch (FileNotFoundException e){
            System.out.println("没有找到指定文件");
        }catch (IOException e){
            System.out.println("文件读写出错");
        }
    }

    /**
     * 组建试卷
     * @param num 试题数量
     * @return 试题列表
     */
    public List<Question> GetExamination(int num)
    {
        ImportQuestions();
        int len = Math.min(questionList.size(), num);
        Collections.shuffle(questionList);
        return questionList.subList(0, len);
    }

    /**
     * 计算分数
     * @param _questionList 试题列表
     * @param _answerList 回答列表
     * @return 获得分数
     */
    public static int GetScore(List<Question> _questionList, List<String> _answerList)
    {
        if (_answerList.size() != _questionList.size()) throw new RuntimeException("回答和题目数量不一致");
        int count = 0;
        for (int i = 0;i < _answerList.size();i++)
        {
            if(_questionList.get(i).CheckAnswer(_answerList.get(i))) count++;
        }
        return 100*count/_answerList.size();
    }

    /**
     * 导出试卷信息
     * @param _questionList 试题列表
     * @param _answerList 回答列表
     * @param seconds 完成用时
     */
    public void ExportTest(List<Question> _questionList, List<String> _answerList, int seconds)
    {
        if (_answerList.size() != _questionList.size()) throw new RuntimeException("回答和题目数量不一致");
        Date time = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String fileName = "test-" + fileFormat.format(time);
        File writeFile = new File("resources/tests/" + fileName + ".txt");
        try{
            BufferedWriter writeText = new BufferedWriter(new FileWriter(writeFile));

            writeText.write("---试卷信息---\n");
            writeText.write("考试时间： " + timeFormat.format(time) + "\n");
            writeText.write("试题数量： " + _questionList.size() + "\n");
            writeText.write("考试时间： " + seconds + "/" + timeSeconds + "\n");
            writeText.write("总共得分： " + GetScore(_questionList, _answerList) + "\n");
            writeText.write("---所有题目---\n");
            writeText.write("序号, 题库序号, 题目, 类型, 选项, 正确答案, 你的答案");

            for(int i = 0;i < _questionList.size();i++)
            {
                writeText.newLine();
                writeText.write((i+1) + "," + _questionList.get(i).ToString() + "," + _answerList.get(i));
            }

            writeText.flush();
            writeText.close();
        }catch (FileNotFoundException e){
            System.out.println("没有找到指定文件");
        }catch (IOException e){
            System.out.println("文件读写出错");
        }
    }
}

/**
 * 试题对象
 * @author 朱洪椿
 */
class Question
{
    public final int index;
    public final String text;
    public final QuestionType type;
    public final List<String> choices;
    public final String key;

    public Question(int _index, String _text, QuestionType _type, List<String> _choices, String _key)
    {
        index = _index;
        text = _text;
        type = _type;
        choices = _choices;
        key = _key;
    }

    /**
     * 检查答案
     * @param answer 回答
     * @return 正确/错误结果
     */
    public Boolean CheckAnswer(String answer)
    {
        return Objects.equals(answer, key);
    }

    /**
     * 将试题类型转换为字符串
     * @param type 试题类型
     * @return 转换后的字符串
     */
    public static String TypeToString(QuestionType type)
    {
        switch (type)
        {

            case choice -> {
                return "单项选择题";
            }
            case judge -> {
                return "判断题";
            }
            case fixing -> {
                return "填空题";
            }
        }
        return null;
    }

    /**
     * 将试题转换为字符串
     * @return 转换后的字符串
     */
    public String ToString()
    {
        StringBuilder string = new StringBuilder(index + "," + text + "," + TypeToString(type) + ",");
        for (int i = 0; i < choices.size(); i++)
        {
            string.append(choices.get(i));
            if (i < choices.size() - 1) string.append(";");
        }
        string.append(",").append(key);
        return string.toString();
    }
}


/**
 * 试题类型枚举
 * @author 朱洪椿
 */
enum QuestionType
{
    choice,
    judge,
    fixing
}
