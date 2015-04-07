package sdc.avoidingproblems.circuits.jung;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Tutorial {

   public static void main(String[] args) {

      SimpleGraphView sgv = new SimpleGraphView(); //We create our graph in here
      // The Layout<V, E> is parameterized by the vertex and edge types
      Layout<String, String> layout = new CircleLayout(sgv.g);
      layout.setSize(new Dimension(300, 300)); // sets the initial size of the space
      // The BasicVisualizationServer<V,E> is parameterized by the edge types
      BasicVisualizationServer<String, String> vv
              = new BasicVisualizationServer<>(layout);
      vv.setPreferredSize(new Dimension(350, 350)); //Sets the viewing area size

      // Setup up a new vertex to paint transformer...
      Transformer<String, Paint> vertexPaint = new Transformer<String, Paint>() {
         @Override
         public Paint transform(String s) {
            return Color.LIGHT_GRAY;
         }
      };

      Transformer<String, String> labelTransformer = new Transformer<String, String>() {
         @Override
         public String transform(String s) {
            return s.split("::")[2];
         }
      };
      Transformer<String, Shape> vertexSize = new Transformer<String, Shape>() {
         @Override
         public Shape transform(String s) {
            Ellipse2D circle = new Ellipse2D.Double(-15, -15, s.length()*5, s.length()*5);
            return circle;
         }
      };
      vv.getRenderContext().setVertexShapeTransformer(vertexSize);
      vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
      vv.getRenderContext().setVertexLabelTransformer(labelTransformer);
      vv.getRenderContext().setEdgeLabelTransformer(labelTransformer);
      vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
      vv.getRenderContext().setLabelOffset(20);

//other operations
      JFrame frame = new JFrame("Simple Graph View");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(vv);
      frame.pack();
      frame.setVisible(true);
   }

   public static class SimpleGraphView {

      Graph<String, String> g;
      Graph<String, String> g2;

      public SimpleGraphView() {
         init();
      }

      private void init() {
         // Graph<V, E> where V is the type of the vertices
         // and E is the type of the edges
         g = new DirectedSparseGraph<>();
         // Add some vertices. From above we defined these to be type Integer.

         g.addVertex("a::a::x");
         g.addVertex("a::a::y");
         g.addVertex("a::a::z");
         g.addVertex("a::a::Pre-Mult");
         g.addVertex("a::a::Plus");
         g.addVertex("a::a::Mult");
         g.addVertex("a::a::Mult");
         // Add some edges. From above we defined these to be of type String
         // Note that the default is for undirected edges.
         g.addEdge("a::a::0", "a::a::x", "a::a::Pre-Mult");
         g.addEdge("a::a::1", "a::a::y", "a::a::Pre-Mult");

      }
   }
}
