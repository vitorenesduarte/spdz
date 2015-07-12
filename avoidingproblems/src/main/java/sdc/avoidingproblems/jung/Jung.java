package sdc.avoidingproblems.jung;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;
import sdc.avoidingproblems.circuit.Circuit;
import sdc.avoidingproblems.circuit.CircuitGenerator;
import sdc.avoidingproblems.circuit.Gate;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Jung {

    private static final String SEP = "::";
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 700;
    private static final int MARGIN_X = 30;
    private static final int MARGIN_Y = 30;
    private static Map<String, Position> positions;
    private static Map<Integer, List<Position>> positionsPerLevel;
    private static int MAX_LEVEL = 0;

    public static void main(String[] args) {
        Circuit circuit = CircuitGenerator.generate(8);
        preview(circuit);
    }

    public static void preview(Circuit circuit) {
        positions = new HashMap();
        int numberOfInputs = circuit.getNumberOfInputs();

        Graph<String, String> graph = new DirectedSparseGraph<>();

        double x = (WIDTH - 2 * MARGIN_X) / (numberOfInputs - 1);
        for (int i = 0; i < numberOfInputs; i++) {
            String inputName = getInputName(i);
            graph.addVertex(inputName);
            positions.put(inputName, new Position(x * i + MARGIN_X, MARGIN_Y, true));
        }

        initPositionsPerLevel(numberOfInputs - 1, numberOfInputs, x);

        List<Gate> gates = circuit.getGates();
        for (int i = 0; i < circuit.getGateCount(); i++) {
            Gate gate = gates.get(i);
            List<Integer> inputEdges = gate.getInputEdges();
            String gateName = getGateName(gate, i + numberOfInputs);
            graph.addVertex(gateName);

            List<String> dependenciesName = new ArrayList(inputEdges.size());
            for (Integer input : inputEdges) {
                String edgeName = getEdgeName(input);
                String dependencyName;
                if (input >= numberOfInputs) { // the dependency is another gate
                    Gate dependencyGate = gates.get(input - numberOfInputs);
                    dependencyName = getGateName(dependencyGate, input);
                } else { // the dependency is an input
                    dependencyName = getInputName(input);
                }

                dependenciesName.add(dependencyName);
                graph.addEdge(edgeName, dependencyName, gateName);
            }

            Position gatePosition = calculateGatePostion(dependenciesName);
            gatePosition.setOccupied(true);
            positions.put(gateName, gatePosition);

        }

        for (String label : positions.keySet()) {
            if (label.startsWith("g")) {
                Position p = positions.get(label);
                p.setY((HEIGHT + 2 * MARGIN_Y) / (MAX_LEVEL + 1) * p.getLevel());
            }
        }

        show(graph, positions);
    }

    private static void initPositionsPerLevel(int numberOfLevels, int numberOfInputs, double xOffset) {
        positionsPerLevel = new HashMap();
        for (int level = 1; level <= numberOfLevels; level++) {
            List<Position> positionsList = new ArrayList();
            double startX = MARGIN_X + xOffset + (xOffset / 2) * level;
            for (int i = 0; i < numberOfInputs - level; i++) {
                positionsList.add(new Position(startX + xOffset * i, 0, false));
            }
            positionsPerLevel.put(level, positionsList);
        }
    }

    private static Position calculateGatePostion(List<String> dependenciesName) {
        int gateLevel = calculateGateLevel(dependenciesName);
        double idealX = calculateIdealX(dependenciesName);

        List<Position> levelPositions = positionsPerLevel.get(gateLevel);
        List<Position> freePositions = new ArrayList();
        for (Position p : levelPositions) {
            if (!p.isOccupied()) {
                freePositions.add(p);
            }
        }

        return findBestPosition(idealX, freePositions, gateLevel);
    }

    private static int calculateGateLevel(List<String> dependenciesName) {
        int max = 0;
        for (String dependency : dependenciesName) {
            int dependencyPositionLevel = positions.get(dependency).getLevel();
            if (dependencyPositionLevel > max) {
                max = dependencyPositionLevel;
            }
        }

        max++;
        if (MAX_LEVEL < max) {
            MAX_LEVEL = max;
        }
        return max;
    }

    /**
     * This method only works for gates with 2 inputs
     */
    private static double calculateIdealX(List<String> dependenciesName) {
        double dependencyOneX = positions.get(dependenciesName.get(0)).getX();
        double dependencyTwoX = positions.get(dependenciesName.get(1)).getX();
        double idealX;

        if (dependencyOneX < dependencyTwoX) {
            idealX = dependencyOneX + (dependencyTwoX - dependencyOneX) / 2;
        } else {
            idealX = dependencyTwoX + (dependencyOneX - dependencyTwoX) / 2;
        }

        return idealX;
    }

    private static Position findBestPosition(double idealX, List<Position> freePositions, int gateLevel) {
        double smallerDifference = Math.abs(freePositions.get(0).getX() - idealX);
        Position best = freePositions.get(0);
        for (int i = 1; i < freePositions.size(); i++) {
            double newDifference = Math.abs(freePositions.get(i).getX() - idealX);
            if (newDifference < smallerDifference) {
                smallerDifference = newDifference;
                best = freePositions.get(i);
            }
        }
        best.setLevel(gateLevel);
        return best;
    }

    private static String getInputName(int index) {
        return "i" + SEP + index;
    }

    private static String getGateName(Gate gate, int index) {
        return "g" + gate.getSemantic() + SEP + index;
    }

    private static String getEdgeName(int index) {
        return "e" + SEP + index;
    }

    private static void show(Graph graph, final Map<String, Position> positions) {
        Transformer<String, Point2D> locationTransformer = new Transformer<String, Point2D>() {
            @Override
            public Point2D transform(String vertex) {
                Position p = positions.get(vertex);
                return new Point2D.Double(p.getX(), p.getY());
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

        JFrame frame = new JFrame("Circuit");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }
}
