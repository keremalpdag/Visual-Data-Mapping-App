import javax.swing.*;

public class Main {
    public static void main(String[] args){

        setLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            FileUploadScreen fileUploadScreen = new FileUploadScreen();
            fileUploadScreen.setVisible(true);
        });
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
