package sdc.avoidingproblems.circuits.jung;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;
import sdc.avoidingproblems.circuits.Circuit;
import sdc.avoidingproblems.circuits.CircuitGenerator;
import sdc.avoidingproblems.circuits.Gate;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Jung {

   private static final String SEP = "::";
   private static final int WIDTH = 1366;
   private static final int HEIGHT = 668;
   private static final int MARGIN_X = 30;
   private static final int MARGIN_Y = 30;
   private static Map<String, Point2D> locations;

   public static void main(String[] args) {
      CircuitGenerator generator = new CircuitGenerator();
      Circuit circuit = generator.generate(4);
      preview(circuit);
   }

   public static void preview(Circuit circuit) {
      locations = new HashMap();
      int numberOfInputs = circuit.getInputSize();
      Graph<String, String> graph = new DirectedSparseGraph<>();

      int numberOfVerticesInLevel = numberOfInputs;

      float x = (WIDTH - 2 * MARGIN_X) / (numberOfVerticesInLevel - 1);
      for (int i = 0; i < numberOfInputs; i++) {
         String inputName = "i" + SEP + i;
         graph.addVertex(inputName);
         locations.put(inputName, new Point2D.Double(x * i + MARGIN_X, 30));
      }

      System.out.println("i:" + numberOfInputs);
      System.out.println("g:" + circuit.getGateCount());
      System.out.println(calculateNumberOfLevels(numberOfInputs, circuit.getGateCount()));

      List<Gate> gates = circuit.getGates();
      for (int i = 0; i < circuit.getGateCount(); i++) {
         Gate gate = gates.get(i);
         List<Integer> inputEdges = gate.getInputEdges();
         String gateName = getGateName(gate, i + numberOfInputs);
         graph.addVertex(gateName);
         locations.put(gateName, new Point2D.Double(15 * (i + 1), 50));

         for (Integer input : inputEdges) {
            String edgeName = "e" + SEP + input;
            if (input >= numberOfInputs) { // the dependency is another gate
               Gate dependencyGate = gates.get(input - numberOfInputs);
               graph.addEdge(edgeName, getGateName(dependencyGate, input), gateName);
            } else { // the dependency is an input
               graph.addEdge(edgeName, "i" + SEP + input, gateName);
            }
         }
         if (i == circuit.getGateCount() - 1) { // the last gate
            String outputName = "gOUTPUT" + SEP;
            String edgeOutputName = "e" + SEP;
            locations.put(outputName, new Point2D.Double(700, 500));

            graph.addVertex(outputName);
            graph.addEdge(edgeOutputName, gateName, outputName);
         }
      }

      show(graph, locations);
   }

   private static String getGateName(Gate gate, int index) {
      return "g" + gate.getSemantic() + SEP + index;
   }

   private static int calculateNumberOfLevels(int numberOfInputs, int numberOfGates) {
      int numberOfLevels = 1;
      while ((numberOfGates = numberOfGates - numberOfInputs) > 0) {
         numberOfLevels++;
      }

      return numberOfLevels;
   }

   private static void show(Graph graph, final Map<String, Point2D> locations) {
      Transformer<String, Point2D> locationTransformer = new Transformer<String, Point2D>() {
         @Override
         public Point2D transform(String vertex) {
            return locations.get(vertex);
         }
      };

      Layout<String, String> layout = new StaticLayout<>(graph, locationTransformer);
      layout.setSize(new Dimension(WIDTH, HEIGHT));
      BasicVisualizationServer<String, String> vv
              = new BasicVisualizationServer<>(layout);
      vv.setPreferredSize(new Dimension(WIDTH, HEIGHT)); //Sets the viewing area size

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
            String[] parts = s.split(SEP);
            if (parts[0].startsWith("g")) {
               return parts[0].substring(1);
            } else if (parts[0].startsWith("e")) {
               return "";
            } else {
               return parts[1];
            }
         }
      };
      Transformer<String, Shape> vertexSize = new Transformer<String, Shape>() {
         @Override
         public Shape transform(String s) {
            Ellipse2D circle = new Ellipse2D.Double(-15, -15, s.length() * 5, s.length() * 5);
            return circle;
         }
      };
      vv.getRenderContext().setVertexShapeTransformer(vertexSize);
      vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
      vv.getRenderContext().setVertexLabelTransformer(labelTransformer);
      vv.getRenderContext().setEdgeLabelTransformer(labelTransformer);
      vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
      vv.getRenderContext().setLabelOffset(20);

//other operations
      JFrame frame = new JFrame("Circuit");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(vv);
      frame.pack();
      frame.setVisible(true);
   }
}
