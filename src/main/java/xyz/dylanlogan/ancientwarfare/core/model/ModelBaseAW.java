package xyz.dylanlogan.ancientwarfare.core.model;

import xyz.dylanlogan.ancientwarfare.core.util.StringTools;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ModelBaseAW {

    int textureWidth = 256;
    int textureHeight = 256;

    HashMap<String, ModelPiece> pieces = new HashMap<String, ModelPiece>();
    private List<ModelPiece> basePieces = new ArrayList<ModelPiece>();

    protected int iterationNum;

    public void renderModel() {
        for (ModelPiece piece : this.getBasePieces()) {
            piece.render(textureWidth, textureHeight);
        }
    }

    public void renderForSelection() {
        iterationNum = 0;
        for (ModelPiece piece : this.getBasePieces()) {
            piece.renderForSelection(textureWidth, textureHeight, this);
        }
    }

    public void renderForEditor(ModelPiece selectedPiece, Primitive selectedPrimitive, List<ModelPiece> selectedPieceParents) {
        for (ModelPiece piece2 : this.getBasePieces()) {
            piece2.renderForEditor(selectedPiece, selectedPrimitive, selectedPieceParents, textureWidth, textureHeight);
        }
        GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
    }

    public void setTextureSize(int width, int height) {
        this.textureWidth = width;
        this.textureHeight = height;
        this.recompilePrimitives();
    }

    public void parseFromLines(List<String> lines) {
        String[] bits;
        for (String line : lines) {
            if (line.toLowerCase(Locale.ENGLISH).startsWith("#")) {
                continue;
            } else if (line.toLowerCase(Locale.ENGLISH).startsWith("texturesize=")) {
                bits = line.split("=")[1].split(",");
                textureWidth = StringTools.safeParseInt(bits[0]);
                textureHeight = StringTools.safeParseInt(bits[1]);
            } else if (line.startsWith("part=")) {
                ModelPiece piece = new ModelPiece(this, line.split("=")[1]);
                addPiece(piece);
            } else if (line.startsWith("box=")) {
                bits = line.split("=")[1].split(",");
                //parse old-style x,y,z, w,h,l
                String parentName = bits[0];
                ModelPiece piece = getPiece(parentName);
                if (piece == null) {
                    throw new IllegalArgumentException("could not construct model, improper piece reference for: " + parentName);
                }
                PrimitiveBox box = new PrimitiveBox(piece);
                box.readFromLine(bits);
                piece.addPrimitive(box);
            } else if (line.toLowerCase(Locale.ENGLISH).startsWith("quad")) {
                bits = line.split("=")[1].split(",");
                //parse old-style x,y,z, w,h,l
                String parentName = bits[0];
                ModelPiece piece = getPiece(parentName);
                if (piece == null) {
                    throw new IllegalArgumentException("could not construct model, improper piece reference for: " + parentName);
                }
                PrimitiveQuad box = new PrimitiveQuad(piece);
                box.readFromLine(bits);
                piece.addPrimitive(box);
            } else if (line.toLowerCase(Locale.ENGLISH).startsWith("triangle")) {
                bits = line.split("=")[1].split(",");
                //parse old-style x,y,z, w,h,l
                String parentName = bits[0];
                ModelPiece piece = getPiece(parentName);
                if (piece == null) {
                    throw new IllegalArgumentException("could not construct model, improper piece reference for: " + parentName);
                }
                PrimitiveTriangle box = new PrimitiveTriangle(piece);
                box.readFromLine(bits);
                piece.addPrimitive(box);
            }
        }
    }

    public List<String> getModelLines() {
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("textureSize=" + textureWidth + "," + textureHeight);
        for (ModelPiece piece : this.basePieces) {
            piece.addPieceLines(lines);
        }
        return lines;
    }

    public void addPiece(ModelPiece piece) {
        pieces.put(piece.getName(), piece);
        if (piece.getParent() == null) {
            getBasePieces().add(piece);
        }
    }

    public void getPieces(List<ModelPiece> input) {
        for (ModelPiece piece : this.basePieces) {
            piece.getPieces(input);
        }
    }

    public void setPieceRotation(String name, float x, float y, float z) {
        ModelPiece piece = this.getPiece(name);
        if (piece == null) {
            return;
        }
        piece.setRotation(x, y, z);
    }

    public ModelPiece getPiece(String name) {
        return this.pieces.get(name);
    }

    public void removePiece(String name) {
        ModelPiece piece = this.getPiece(name);
        removePiece(piece);
    }

    public void removePiece(ModelPiece piece) {
        this.pieces.remove(piece.getName());
        this.basePieces.remove(piece);
    }

    public List<ModelPiece> getBasePieces() {
        return basePieces;
    }

    public Primitive getPrimitive(int num) {
        Primitive prim;
        this.iterationNum = 0;
        for (ModelPiece p : basePieces) {
            prim = p.getPickedPrimitive(num, this);
            if (prim != null) {
                return prim;
            }
        }
        return null;
    }

    public void recompilePrimitives() {
        for (ModelPiece p : this.basePieces) {
            p.recompilePiece();
        }
    }

    public int textureWidth() {
        return textureWidth;
    }

    public int textureHeight() {
        return textureHeight;
    }

}
