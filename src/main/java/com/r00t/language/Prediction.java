package com.r00t.language;

import java.io.IOException;

public class Prediction {
    public static void main(String[] args) throws InterruptedException, IOException {
        writeSignature();
        PredictionProperties.init(args.length == 0 ? null : args[0]);
        new MLPClassifierLinear().train();
        writeSignature();
    }

    private static void writeSignature() {
        System.out.println("\n\n        ___           ___                   ___           ___     ");
        System.out.println("       /\\  \\         /\\__\\      ___        /\\  \\         /\\__\\    ");
        System.out.println("      /::\\  \\       /:/  /     /\\  \\      /::\\  \\       /:/  /    ");
        System.out.println("     /:/\\:\\  \\     /:/  /      \\:\\  \\    /:/\\:\\  \\     /:/__/     ");
        System.out.println("    /:/  \\:\\  \\   /:/  /       /::\\__\\  /:/  \\:\\  \\   /::\\__\\____ ");
        System.out.println("   /:/__/ \\:\\__\\ /:/__/     __/:/\\/__/ /:/__/ \\:\\__\\ /:/\\:::::\\__\\");
        System.out.println("   \\:\\  \\  \\/__/ \\:\\  \\    /\\/:/  /    \\:\\  \\  \\/__/ \\/_|:|~~|~   ");
        System.out.println("    \\:\\  \\        \\:\\  \\   \\::/__/      \\:\\  \\          |:|  |    ");
        System.out.println("     \\:\\  \\        \\:\\  \\   \\:\\__\\       \\:\\  \\         |:|  |    ");
        System.out.println("      \\:\\__\\        \\:\\__\\   \\/__/        \\:\\__\\        |:|  |    ");
        System.out.println("       \\/__/         \\/__/                 \\/__/         \\|__|    ");
        System.out.println("\n                       Create, Digital, Dreams                         \n\n");
    }
}
