import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 答题子页面
 * @author 朱洪椿
 */
public class QuestionPanel
{
    public final JPanel basePanel;
    private final TestWindow testWindow;
    private final JPanel contentPanel;
    private final JButton leftButton;
    private final JButton rightButton;
    private final TopBar topBar;
    private final BottomBar bottomBar;
    private Timer timer;
    private int index = 0;
    private final int listNum;
    private final List<Question> questionList;
    private final List<String> answerList;

    public QuestionPanel(TestWindow _testWindow, List<Question> _questionList)
    {
        testWindow = _testWindow;
        answerList = new ArrayList<>();
        listNum = _questionList.size();
        questionList = _questionList;
        // 基础面板
        basePanel = new JPanel(new BorderLayout());
        basePanel.setBackground(new Color(254, 250, 224));
        // 顶栏
        topBar = new TopBar();
        basePanel.add(topBar.panel, BorderLayout.NORTH);
        // 底栏
        bottomBar = new BottomBar(this, questionList);
        basePanel.add(bottomBar.panel, BorderLayout.SOUTH);

        CardLayout layout = new CardLayout();
        contentPanel = new JPanel(layout);
        basePanel.add(contentPanel, BorderLayout.CENTER);
        // 按钮：上一个
        leftButton = CreateButton(ImagePath.leftImage, BorderLayout.WEST);
        leftButton.addActionListener(e -> {
            index--;
            layout.previous(contentPanel);
            topBar.Update(index, listNum, questionList.get(index).type);
            UpdateButton();
        });
        // 按钮：下一个
        rightButton = CreateButton(ImagePath.rightImage, BorderLayout.EAST);
        rightButton.addActionListener(e -> {
            index++;
            layout.next(contentPanel);
            topBar.Update(index, listNum, questionList.get(index).type);
            UpdateButton();
        });
        // 生成问题卡片
        int i = 0;
        for (Question question : questionList)
        {
            switch (question.type)
            {
                case choice -> contentPanel.add(new ChoiceQuestionPanel(i, question, this).panel, String.valueOf(i));
                case judge -> contentPanel.add(new JudgeQuestionPanel(i, question, this).panel, String.valueOf(i));
                case fixing -> contentPanel.add(new FixingQuestionPanel(i, question, this).panel, String.valueOf(i));
            }
            i++;
            answerList.add("");
        }
        // 更新和显示窗口内容
        topBar.Update(index, listNum, questionList.get(index).type);
        layout.first(contentPanel);
        UpdateButton();
        UpdateBottomBar();
    }

    /**
     * 创建两侧的按钮
     * @param path 按钮图片路径
     * @param pos 按钮位置
     * @return 创建的 JButton 对象
     */
    private JButton CreateButton(String path, String pos)
    {
        JButton button = new JButton(new ImageIcon(path));
        button.setBackground(new Color(42, 157, 143));
        button.setBorderPainted(false);
        basePanel.add(button, pos);
        return button;
    }

    /**
     * 更新按钮：可用/不可用
     */
    private void UpdateButton()
    {
        leftButton.setEnabled(true);
        rightButton.setEnabled(true);
        if (index <= 0) leftButton.setEnabled(false);
        if (index >= listNum - 1) rightButton.setEnabled(false);
    }

    /**
     * 更新底栏：已回答的题目数
     */
    public void UpdateBottomBar()
    {
        int count = 0;
        for (String answer : answerList)
        {
            if (Objects.equals(answer, "")) count++;
        }
        bottomBar.Update(count);
    }

    /**
     * 开始考试：计时开始
     */
    public void Start()
    {
        timer = new Timer();
        timer.AddListener(e -> {
            if (TestSystem.instance.timeSeconds - e.getID() <= 0)
            {
                JOptionPane.showMessageDialog(basePanel, "答题时间结束", "交卷提示", JOptionPane.INFORMATION_MESSAGE);
                testWindow.SubmitTest(questionList, answerList, timer.GetSeconds());
            }
            topBar.Update(TestSystem.instance.timeSeconds - e.getID());
        });
        timer.Start();
    }

    /**
     * 停止考试：计时停止
     */
    public void Stop()
    {
        if (timer!=null) timer.Stop();
        timer = null;
    }

    /**
     * 交卷
     */
    public void Submit()
    {
        testWindow.SubmitTest(questionList, answerList, timer.GetSeconds());
    }

    /**
     * 提交答案
     * @param index 题目序号
     * @param answer 答案
     */
    public void SubmitAnswer(int index, String answer)
    {
        answerList.set(index, answer);
    }
}

/**
 * 顶栏
 * @author 朱洪椿
 */
class TopBar
{
    public final JPanel panel;
    private final JLabel indexLabel;
    private final JLabel typeLabel;
    private final JLabel timeLabel;
    public TopBar()
    {
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 130, 5));
        panel.setBorder(new EmptyBorder(5, 5, 5,5));
        panel.setBackground(new Color(38, 70, 83));
        // 题目序号
        indexLabel = new JLabel(0 + " / " + 0, JLabel.CENTER);
        indexLabel.setFont(new Font("黑体", Font.BOLD ,20));
        indexLabel.setForeground(Color.white);
        indexLabel.setPreferredSize(new Dimension(150, 50));
        // 题目类型
        typeLabel = new JLabel(Question.TypeToString(QuestionType.choice), JLabel.CENTER);
        typeLabel.setForeground(Color.white);
        typeLabel.setFont(new Font("黑体", Font.PLAIN ,20));
        typeLabel.setPreferredSize(new Dimension(150, 50));
        // 剩余考试时间
        timeLabel = new JLabel("答题时间还剩 12:30", JLabel.RIGHT);
        timeLabel.setFont(new Font("黑体", Font.PLAIN ,16));
        timeLabel.setPreferredSize(new Dimension(150, 50));
        timeLabel.setForeground(new Color(231, 111, 81));

        panel.add(indexLabel);
        panel.add(typeLabel);
        panel.add(timeLabel);
    }

    /**
     * 更新剩余时间
     * @param time 剩余时间
     */
    public void Update(int time)
    {
        int seconds = time % 60;
        int minute = (time - seconds) / 60;
        timeLabel.setText("答题时间还剩 " + minute + ":" + seconds);
    }

    /**
     * 更新信息
     * @param index 题目序号
     * @param number 题目总数
     * @param type 题目类型
     */
    public void Update(int index, int number, QuestionType type)
    {
        index++;
        indexLabel.setText(index + " / " + number);
        typeLabel.setText(Question.TypeToString(type));
    }
}

/**
 * 底栏
 */
class BottomBar
{
    public final JPanel panel;
    private final JLabel finishState;
    private final QuestionPanel parent;
    private final List<Question> questionList;

    public BottomBar(QuestionPanel questionPanel, List<Question> _questionList)
    {
        parent = questionPanel;
        questionList = _questionList;
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setBackground(new Color(38, 70, 83));
        // 交卷按钮
        JButton finishButton = new JButton("交卷");
        finishButton.setBackground(Color.white);
        finishButton.setForeground(new Color(42, 157, 143));
        finishButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(parent.basePanel, "确定要交卷吗？", "交卷提示", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) parent.Submit();
        });
        panel.add(finishButton);
        // 题目完成计数
        finishState = new JLabel("已完成题目 0/0");
        finishState.setForeground(new Color(255, 255, 255));
        panel.add(finishState);
    }

    /**
     * 更新题目完成计数信息
     * @param count 完成的题目数量
     */
    public void Update(int count)
    {
        finishState.setText("已完成题目 " + (questionList.size() - count) + "/" + questionList.size());
    }
}

/**
 * 基础题目卡片
 */
abstract class BaseQuestionPanel
{
    public final JPanel panel;
    protected final int index;
    protected JButton submitButton;
    private final QuestionPanel parent;


    public BaseQuestionPanel(QuestionPanel questionPanel, Question _data, int _index)
    {
        index = _index;
        parent = questionPanel;
        panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 20));
        panel.setBackground(new Color(202, 240, 248));
        panel.setBorder(new EmptyBorder(20,20,20,20));
        // 显示题目内容
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel questionInfo = new JLabel(_data.text);
        questionInfo.setFont(new Font("黑体", Font.PLAIN, 18));
        infoPanel.add(questionInfo);
        infoPanel.setOpaque(false);
        panel.add(infoPanel);
        // 分割线
        JPanel line = new JPanel();
        line.setOpaque(false);
        line.setPreferredSize(new Dimension(700, 1));
        panel.add(line);
    }

    /**
     * 获取用户的回答
     * @return 回答
     */
    abstract String GetAnswer();

    /**
     * 创建确定按钮
     */
    protected void CreateButton()
    {
        JPanel submitPanel;
        submitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        submitPanel.setPreferredSize(new Dimension(700, 50));
        submitPanel.setBorder(new EmptyBorder(5, 5, 5,5));
        submitPanel.setOpaque(false);
        submitButton = new JButton("确定");
        submitButton.setBackground(new Color(231, 111, 81));
        submitButton.setForeground(Color.white);
        submitButton.addActionListener(e -> {
            parent.SubmitAnswer(index, GetAnswer());
            parent.UpdateBottomBar();
            submitButton.setBackground(new Color(244, 162, 97));
        });
        submitPanel.add(submitButton);
        panel.add(submitPanel);
    }
}

/**
 * 选择题卡片
 * @author 朱洪椿
 */
class ChoiceQuestionPanel extends BaseQuestionPanel
{
    private String answer = "";
    public ChoiceQuestionPanel(int index, Question data, QuestionPanel questionPanel)
    {
        super(questionPanel, data, index);
        JPanel choicePanel = new JPanel(new GridLayout(data.choices.size(), 1, 10, 5));
        choicePanel.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        for (String choice : data.choices)
        {
            JRadioButton button = new JRadioButton(choice);
            button.setFont(new Font("黑体", Font.PLAIN, 15));
            button.setOpaque(false);
            button.addActionListener(e -> {
                answer = button.getText();
                submitButton.setBackground(new Color(231, 111, 81));
            });
            group.add(button);
            choicePanel.add(button);
        }
        panel.add(choicePanel);
        CreateButton();
    }

    @Override
    protected String GetAnswer() {
        return answer;
    }
}

/**
 * 判断题卡片
 * @author 朱洪椿
 */
class JudgeQuestionPanel extends BaseQuestionPanel
{
    private String answer = "";
    public JudgeQuestionPanel(int index, Question data, QuestionPanel questionPanel)
    {
        super(questionPanel, data, index);
        JPanel choicePanel = new JPanel(new GridLayout(2, 1, 10, 5));
        choicePanel.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        String[] choiceList = {"正确", "错误"};
        for (String choice : choiceList)
        {
            JRadioButton button = new JRadioButton(choice);
            button.setFont(new Font("黑体", Font.PLAIN, 15));
            button.setOpaque(false);
            button.addActionListener(e -> {
                answer = button.getText();
                submitButton.setBackground(new Color(231, 111, 81));
            });
            group.add(button);
            choicePanel.add(button);
        }
        panel.add(choicePanel);
        CreateButton();
    }

    @Override
    protected String GetAnswer() {
        return answer;
    }
}

/**
 * 填空题卡片
 * @author 朱洪椿
 */
class FixingQuestionPanel extends BaseQuestionPanel
{
    private final JTextField field;
    public FixingQuestionPanel(int index, Question data, QuestionPanel questionPanel)
    {
        super(questionPanel, data, index);
        JPanel fixingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        fixingPanel.setOpaque(false);
        field = new JTextField();
        field.setBorder(new LineBorder(new Color(42, 157, 143)));
        field.setPreferredSize(new Dimension(80, 25));
        field.setBackground(Color.white);
        field.addActionListener(e -> submitButton.setBackground(new Color(231, 111, 81)));
        fixingPanel.add(field);
        panel.add(fixingPanel);
        CreateButton();
    }

    @Override
    protected String GetAnswer() {
        return field.getText();
    }
}