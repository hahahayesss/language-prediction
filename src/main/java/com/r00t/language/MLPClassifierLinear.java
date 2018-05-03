package com.r00t.language;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MLPClassifierLinear {
    public void train() throws InterruptedException, IOException {
        LanguageRecordReader recordReader = new LanguageRecordReader();

        Long startTime = System.currentTimeMillis();
        recordReader.initialize(null);
        Long endTime = System.currentTimeMillis();

        System.out.println("\n- INFO > RecordReader has initialized");
        System.out.println(
                "- INFO > It took " + (endTime - startTime) + "ms " +
                        "(" + TimeUnit.MILLISECONDS.toMinutes(endTime - startTime) + "m " +
                        (TimeUnit.MILLISECONDS.toSeconds(endTime - startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(endTime - startTime))) + "s)\n"
        );

        DataSetIterator dataSetIterator = new RecordReaderDataSetIterator(
                recordReader,
                PredictionProperties.getBatchSize(),
                PredictionProperties.getNumInputs(),
                PredictionProperties.getNumOutputs()
        );

        // - Configuration >>>
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(PredictionProperties.getSeed())
//                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .biasInit(1)
                .l2(1e-4)
                .updater(new Nesterovs(PredictionProperties.getLearningRate(), 0.9))
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(PredictionProperties.getNumInputs())
                        .nOut(PredictionProperties.getNumHiddenNodes())
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new DenseLayer.Builder()
                        .nIn(PredictionProperties.getNumHiddenNodes())
                        .nOut(PredictionProperties.getNumHiddenNodes())
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.SOFTMAX)
                        .nIn(PredictionProperties.getNumHiddenNodes())
                        .nOut(PredictionProperties.getNumOutputs())
                        .build())
                .pretrain(false)
                .backprop(true)
                .build();
        // <<< Configuration -

        // - TRAIN >>>
        Long startTrainTime = System.currentTimeMillis();
        MultiLayerNetwork model = new MultiLayerNetwork(configuration);
        model.init();

        // - - Server >>>
        if (PredictionProperties.getUIServer()) {
            UIServer uiServer = UIServer.getInstance();
            StatsStorage statsStorage = new InMemoryStatsStorage();
            uiServer.attach(statsStorage);
            model.setListeners(new StatsListener(statsStorage));
        } else
            model.setListeners(new ScoreIterationListener(10000));
        // <<< Server - -

        for (int x = 0; x < PredictionProperties.getNEpochs(); x++) {
            while (dataSetIterator.hasNext())
                model.fit(dataSetIterator.next());
            dataSetIterator.reset();
        }
        Long endTrainTime = System.currentTimeMillis();
        // <<< TRAIN -

        System.out.println("\n- INFO > Train is finished");
        System.out.println(
                "- INFO > It took " + (endTrainTime - startTrainTime) + "ms " +
                        "(" + TimeUnit.MILLISECONDS.toMinutes(endTime - startTime) + "m " +
                        (TimeUnit.MILLISECONDS.toSeconds(endTrainTime - startTrainTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(endTrainTime - startTrainTime))) + "s)\n"
        );

        // - Save >>>
        ModelSerializer.writeModel(model, PredictionProperties.getOutputFileLocation(), true);
        // <<< Save -

        // - Evaluation >>>
        if (PredictionProperties.getEvaluation()) {
            DataSetIterator dataSetIteratorTest = new RecordReaderDataSetIterator(
                    recordReader,
                    PredictionProperties.getBatchSize(),
                    PredictionProperties.getNumInputs(),
                    PredictionProperties.getNumOutputs()
            );

            Long startEvaluatingTime = System.currentTimeMillis();
            Evaluation evaluation = new Evaluation(PredictionProperties.getNumOutputs());
            while (dataSetIteratorTest.hasNext()) {
                DataSet dataSet = dataSetIteratorTest.next();
                INDArray output = model.output(dataSet.getFeatureMatrix());
                evaluation.eval(dataSet.getLabels(), output);
            }
            Long endEvaluatingTime = System.currentTimeMillis();

            System.out.println(evaluation.stats());
            System.out.println("\n- INFO > Evaluation completed");
            System.out.println("- INFO > It took " + (endEvaluatingTime - startEvaluatingTime) + "ms");
        } else
            System.out.println("- WARNING > Evaluation False > For Learning Algorithms Evaluation is important");
        // <<< Evaluation -
    }
}
