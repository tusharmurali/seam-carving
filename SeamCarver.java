/* *****************************************************************************
 *  Name: Tushar Muralidharan
 *  Date: December 28, 2022
 *  Description: A mutable data type that implements the seam carving algorithm for content-aware image resizing.
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private Picture picture;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        int right = x + 1 < width() ? picture.getRGB(x + 1, y) : picture.getRGB(0, y);
        int left = x - 1 >= 0 ? picture.getRGB(x - 1, y) : picture.getRGB(width() - 1, y);
        int down = y + 1 < height() ? picture.getRGB(x, y + 1) : picture.getRGB(x, 0);
        int up = y - 1 >= 0 ? picture.getRGB(x, y - 1) : picture.getRGB(x, height() - 1);

        int rx = ((right >> 16) & 0xFF) - ((left >> 16) & 0xFF);
        int gx = ((right >> 8) & 0xFF) - ((left >> 8) & 0xFF);
        int bx = (right & 0xFF) - (left & 0xFF);
        int ry = ((down >> 16) & 0xFF) - ((up >> 16) & 0xFF);
        int gy = ((down >> 8) & 0xFF) - ((up >> 8) & 0xFF);
        int by = (down & 0xFF) - (up & 0xFF);

        int dx = rx * rx + gx * gx + bx * bx;
        int dy = ry * ry + gy * gy + by * by;

        return Math.sqrt(dx + dy);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[] seam = new int[width()];

        double[][] energy = new double[width()][height()];
        double[][] energyTo = new double[width()][height()];
        int[][] pixelTo = new int[width()][height()];

        for (int i = 0; i < height(); i++) {
            energy[0][i] = energy(0, i);
            energyTo[0][i] = energy[0][i];
            for (int j = 1; j < width(); j++) {
                energy[j][i] = energy(j, i);
                energyTo[j][i] = Double.POSITIVE_INFINITY;
            }
        }

        for (int i = 0; i < width() - 1; i++) {
            for (int j = 0; j < height(); j++) {
                if (j - 1 >= 0 && energyTo[i + 1][j - 1] > energyTo[i][j] + energy[i + 1][j - 1]) {
                    energyTo[i + 1][j - 1] = energyTo[i][j] + energy[i + 1][j - 1];
                    pixelTo[i + 1][j - 1] = j;
                }
                if (energyTo[i + 1][j] > energyTo[i][j] + energy[i + 1][j]) {
                    energyTo[i + 1][j] = energyTo[i][j] + energy[i + 1][j];
                    pixelTo[i + 1][j] = j;
                }
                if (j + 1 < height() && energyTo[i + 1][j + 1] > energyTo[i][j] + energy[i + 1][j + 1]) {
                    energyTo[i + 1][j + 1] = energyTo[i][j] + energy[i + 1][j + 1];
                    pixelTo[i + 1][j + 1] = j;
                }
            }
        }

        double minEnergy = energyTo[width() - 1][0];
        int minPixel = 0;
        for (int i = 1; i < height(); i++) {
            if (energyTo[width() - 1][i] < minEnergy) {
                minEnergy = energyTo[width() - 1][i];
                minPixel = i;
            }
        }

        seam[width() - 1] = minPixel;
        for (int i = width() - 1; i > 0; i--)
            seam[i - 1] = pixelTo[i][seam[i]];
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] seam = new int[height()];

        double[][] energy = new double[width()][height()];
        double[][] energyTo = new double[width()][height()];
        int[][] pixelTo = new int[width()][height()];

        for (int i = 0; i < width(); i++) {
            energy[i][0] = energy(i, 0);
            energyTo[i][0] = energy[i][0];
            for (int j = 1; j < height(); j++) {
                energy[i][j] = energy(i, j);
                energyTo[i][j] = Double.POSITIVE_INFINITY;
            }
        }

        for (int i = 0; i < height() - 1; i++) {
            for (int j = 0; j < width(); j++) {
                if (j - 1 >= 0 && energyTo[j - 1][i + 1] > energyTo[j][i] + energy[j - 1][i + 1]) {
                    energyTo[j - 1][i + 1] = energyTo[j][i] + energy[j - 1][i + 1];
                    pixelTo[j - 1][i + 1] = j;
                }
                if (energyTo[j][i + 1] > energyTo[j][i] + energy[j][i + 1]) {
                    energyTo[j][i + 1] = energyTo[j][i] + energy[j][i + 1];
                    pixelTo[j][i + 1] = j;
                }
                if (j + 1 < width() && energyTo[j + 1][i + 1] > energyTo[j][i] + energy[j + 1][i + 1]) {
                    energyTo[j + 1][i + 1] = energyTo[j][i] + energy[j + 1][i + 1];
                    pixelTo[j + 1][i + 1] = j;
                }
            }
        }

        double minEnergy = energyTo[0][height() - 1];
        int minPixel = 0;
        for (int i = 1; i < width(); i++) {
            if (energyTo[i][height() - 1] < minEnergy) {
                minEnergy = energyTo[i][height() - 1];
                minPixel = i;
            }
        }

        seam[height() - 1] = minPixel;
        for (int i = height() - 1; i > 0; i--)
            seam[i - 1] = pixelTo[seam[i]][i];
        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException("Seam is null");
        if (seam.length != width())
            throw new IllegalArgumentException("Seam is of the wrong length");
        if (height() <= 1)
            throw new IllegalArgumentException("Height of the current picture is less than or equal to 1");
        Picture temp = new Picture(width(), height() - 1);
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= height() || (i < seam.length - 1 && (
                    seam[i + 1] - seam[i] > 1 || seam[i + 1] - seam[i] < -1)))
                throw new IllegalArgumentException("Invalid seam");

            for (int j = 0; j < height() - 1; j++) {
                if (j < seam[i]) temp.setRGB(i, j, picture.getRGB(i, j));
                else temp.setRGB(i, j, picture.getRGB(i, j + 1));
            }
        }
        picture = temp;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException("Seam is null");
        if (seam.length != height())
            throw new IllegalArgumentException("Seam is of the wrong length");
        if (width() <= 1)
            throw new IllegalArgumentException("Width of the current picture is less than or equal to 1");
        Picture temp = new Picture(width() - 1, height());
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width() || (i < seam.length - 1 && (
                    seam[i + 1] - seam[i] > 1 || seam[i + 1] - seam[i] < -1)))
                throw new IllegalArgumentException("Invalid seam");

            for (int j = 0; j < width() - 1; j++) {
                if (j < seam[i]) temp.setRGB(j, i, picture.getRGB(j, i));
                else temp.setRGB(j, i, picture.getRGB(j + 1, i));
            }
        }
        picture = temp;
    }

    // unit testing
    public static void main(String[] args) {

    }

}
