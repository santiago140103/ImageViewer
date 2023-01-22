package imageviewer;

import imageviewer.architecture.Image;
import imageviewer.architecture.ImageDisplay;
import imageviewer.architecture.ImageLoader;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import imageviewer.architecture.ImagePresenter;
import java.awt.BorderLayout;



public class MainFrame extends JFrame {

    private final ImagePanel imagePanel;
    private JFileChooser fileChooser;

    public MainFrame() {
        this.setTitle("Image Viewer");
        this.setSize(800,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.add(imagePanel = new ImagePanel());
        this.getContentPane().add(chooser(),BorderLayout.NORTH);
        
    }
    
    public ImageDisplay imageDisplay() {
        return imagePanel;
    }
    
    private JPanel chooser() {
        JPanel panel = new JPanel();
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JButton button = new JButton("Choose a folder");
        button.addActionListener(listener());
        panel.add(button);
        
        return panel;
    }
    
    public ActionListener listener() {
        return (ActionEvent g) -> {
            fileChooser.showOpenDialog(fileChooser);
            File file = new File(fileChooser.getSelectedFile().toString());
            FileImageLoader loader = new FileImageLoader(file);        
            Image image = loader.load();
            ImageDisplay imageDisplay = imageDisplay();
            ImagePresenter.with(loader.load(), imageDisplay); 
        }; 
    }

    private static class ImagePanel extends JPanel implements ImageDisplay {
        
        private final List<Order> orders;
        private DragEvent onDragged = DragEvent::Null;
        private NotifyEvent onReleased = NotifyEvent::Null;
        private int x;

        private ImagePanel() {
            this.orders = new ArrayList<>();
            this.addMouseListener(new MouseListener() {
                
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    x = e.getX();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    onReleased.handle(e.getX()-x);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
            this.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    onDragged.handle(e.getX()-x);
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                }
            });
        }

        @Override
        public void paint(Graphics g) {
            clean(g);
            for (Order order : orders) 
                g.drawImage(order.image, order.x(this.getWidth()), order.y(this.getHeight()), order.width, order.height, null);                
           
        }

        private void clean(Graphics g) {
            g.setColor(Color.white);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }

        @Override
        public void clear() {
            orders.clear();
        }

        @Override
        public void paint(Object data, int offset, double ratio) {
            orders.add(new Order(data, offset, ratio));
            repaint();
        }

        @Override
        public void onDragged(DragEvent event) {
            this.onDragged = event;
        }

        @Override
        public void onReleased(NotifyEvent event) {
            this.onReleased = event;
        }

        @Override
        public int width() {
            return this.getWidth();
        }
        
    }

    private static class Order {
        public final BufferedImage image;
        public final int offset;
        public final int width;
        public final int height;

        public Order(Object data, int offset, double ratio)  {
            this.image = (BufferedImage) data;
            this.offset = offset;
            this.width = (int) (image.getWidth() * ratio);
            this.height = (int) (image.getHeight() * ratio);
        }
        
        public int x(int width) {
            return offset;
        }
        
        public int y(int height) {
            return (height - this.height) / 2;
        }
        
    }
                
            
    
    
    
}
