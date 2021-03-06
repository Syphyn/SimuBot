package Triton.Computation.AStar;

import java.util.ArrayList;

import Triton.Config.PathfinderConfig;
import Triton.Shape.Circle2D;
import Triton.Shape.Line2D;
import Triton.Shape.Vec2D;

public class Grid {
    private double worldSizeX;
    private double worldSizeY;
    private Node[][] nodes;
    private int numRows, numCols;

    public Grid(double worldSizeX, double worldSizeY) {
        this.worldSizeX = worldSizeX;
        this.worldSizeY = worldSizeY;
        numCols = (int) Math.round(worldSizeX / PathfinderConfig.NODE_DIAMETER);
        numRows = (int) Math.round(worldSizeY / PathfinderConfig.NODE_DIAMETER);
        createGrid();
    }

    private Vec2D gridPosToWorldPos(int row, int col) {
        return new Vec2D(col * PathfinderConfig.NODE_DIAMETER + PathfinderConfig.NODE_RADIUS - worldSizeX / 2,
                -row * PathfinderConfig.NODE_DIAMETER - PathfinderConfig.NODE_RADIUS + worldSizeY / 2);
    }

    private int[] worldPosToGridPos(Vec2D worldPos) {
        int[] res = { (int) Math.round((worldSizeY / 2 - worldPos.y) / PathfinderConfig.NODE_DIAMETER - 0.5),
                (int) Math.round((worldPos.x + worldSizeX / 2 - PathfinderConfig.NODE_RADIUS)
                        / PathfinderConfig.NODE_DIAMETER), };
        return res;
    }

    public Node nodeFromWorldPos(Vec2D worldPos) {
        int[] gridPos = worldPosToGridPos(worldPos);
        return nodes[gridPos[0]][gridPos[1]];
    }

    private void createGrid() {
        nodes = new Node[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Vec2D worldPos = gridPosToWorldPos(row, col);
                nodes[row][col] = new Node(worldPos, row, col);
            }
        }
    }

    public void updateGrid(ArrayList<Circle2D> obstacles) {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Node node = nodes[row][col];
                Vec2D nodeWorldPos = node.getWorldPos();
                if (nodeWorldPos.x < -worldSizeX / 2 + PathfinderConfig.SAFE_DIST
                        || nodeWorldPos.x > worldSizeX / 2 - PathfinderConfig.SAFE_DIST
                        || nodeWorldPos.y < -worldSizeY / 2 + PathfinderConfig.SAFE_DIST
                        || nodeWorldPos.y > worldSizeY / 2 - PathfinderConfig.SAFE_DIST) {
                    node.setWalkable(false);
                } else {
                    node.setWalkable(true);
                }
            }
        }

        for (Circle2D obstacle : obstacles) {
            ArrayList<Node> toCheck = getNodesToCheck(obstacle);
            for (Node node : toCheck) {
                if (node.getWalkable() && !checkWalkable(node, obstacle))
                    node.setWalkable(false);
            }
        }
    }

    public ArrayList<Node> getNodesToCheck(Circle2D obstacle) {
        Vec2D center = obstacle.center;
        double radius = obstacle.radius + PathfinderConfig.SAFE_DIST;

        Vec2D topLeft = new Vec2D(center.x - radius, center.y + radius);
        Vec2D botRight = new Vec2D(center.x + radius, center.y - radius);

        int[] topLeftGridPos = worldPosToGridPos(topLeft);
        int[] botRightGridPos = worldPosToGridPos(botRight);

        ArrayList<Node> toCheck = new ArrayList<Node>();
        for (int row = topLeftGridPos[0]; row <= botRightGridPos[0]; row++)
            for (int col = topLeftGridPos[1]; col <= botRightGridPos[1]; col++)
                toCheck.add(nodes[row][col]);
        return toCheck;
    }

    public boolean checkWalkable(Node node, Circle2D obstacle) {
        double unwalkableDist = obstacle.radius + PathfinderConfig.SAFE_DIST;
        if (Vec2D.dist(node.getWorldPos(), obstacle.center) <= unwalkableDist)
            return false;
        return true;
    }

    public ArrayList<Node> getNeighbors(Node node) {
        ArrayList<Node> neighbors = new ArrayList<Node>();
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset++) {
                if (rowOffset == 0 && colOffset == 0)
                    continue;

                int checkRow = node.getRow() + rowOffset;
                int checkCol = node.getCol() + colOffset;

                if (checkRow >= 0 && checkRow < numRows && checkCol >= 0 && checkCol < numCols) {
                    neighbors.add(nodes[checkRow][checkCol]);
                }
            }
        }
        return neighbors;
    }

    public boolean checkLineOfSight(Node nodeA, Node nodeB) {
        Vec2D pointA = nodeA.getWorldPos();
        Vec2D pointB = nodeB.getWorldPos();
        Line2D line = new Line2D(pointA, pointB);
        double totalDist = line.length();
        int moveCount = (int) totalDist / PathfinderConfig.NODE_RADIUS;
        Vec2D dir = line.getDir();
        Vec2D moveAdd = dir.mult(PathfinderConfig.NODE_RADIUS);
        
        Vec2D currentPos = new Vec2D(pointA);
        for (int i = 0; i < moveCount; i++) {
            currentPos = currentPos.add(moveAdd);
            Node currentNode = nodeFromWorldPos(currentPos);
            if (!currentNode.getWalkable())
                return false;
        }
        return true;
    }

    public Node[][] getNodes() {
        return nodes;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }
}