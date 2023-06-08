import javax.swing.*;
import java.awt.*;

/**
 * 主窗口行为
 * @author 朱洪椿
 */
public class MainWindow {
    private JFrame mainFrame;
    private Container mainContainer;

    public MainWindow(){
        CreateBaseFrame();
        EventSystem eventSystem = new EventSystem();
        new TestSystem();

        // 添加封面图像
        JLabel coverImg = new JLabel();
        coverImg.setIcon(new ImageIcon(ImagePath.coverImage));
        mainContainer.add(coverImg);
        // 添加按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(254, 250, 224));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 300, 60));
        buttonPanel.setPreferredSize(new Dimension(484, 500));
        mainContainer.add(buttonPanel);
        // 注册按钮点击事件
        eventSystem.startTest = CreateButton("开始测试", new Color(42, 157, 143) ,buttonPanel);
        eventSystem.openHistory = CreateButton("历史记录", new Color(233, 196, 106), buttonPanel);
        eventSystem.openSettings = CreateButton("设置", new Color(231, 111, 81), buttonPanel);
        eventSystem.RegisterButton(mainFrame);
        // 显示窗口
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    /**
     * 创建和配置基础窗口
     */
    private void CreateBaseFrame()
    {
        mainFrame = new JFrame("测验系统");
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setBounds(dimension.width/2-400, dimension.height/2-250, 800, 500);
        mainFrame.setResizable(false);
        mainFrame.setIconImage(new ImageIcon(ImagePath.iconImage).getImage());
        mainContainer = mainFrame.getContentPane();
        mainContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    }

    /**
     * 创建按钮
     * @param text 按钮文本
     * @param color 按钮颜色
     * @param container 父容器
     * @return 生成的 JButton 对象
     */
    private JButton CreateButton(String text, Color color, Container container){
        JButton newButton = new JButton(text);
        newButton.setBackground(new Color(255, 255, 255));
        newButton.setPreferredSize(new Dimension(120, 60));
        newButton.setBorder(BorderFactory.createLineBorder(color, 3, true));
        newButton.setFont(new Font("黑体", Font.PLAIN, 16));
        newButton.setBackground(color.brighter());
        container.add(newButton);
        return newButton;
    }
}
