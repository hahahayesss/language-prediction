package com.r00t.language;

import org.datavec.api.split.FileSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;

public class MLPClassifierLinear {
    public void train() throws InterruptedException, IOException {
        int seed = 123456;
        double learningRate = 0.005;
        int batchSize = 128;
        int nEpochs = 100;
        int numInputs = 20;
        int numOutputs = 5;
        int numHiddenNodes = 45;

        LanguageRecordReader recordReader = new LanguageRecordReader(numInputs);
        recordReader.initialize(new FileSplit(new File("C:\\Users\\hahah\\Desktop\\ML_IN_OUT\\FULL_SIZE")));

        DataSetIterator dataSetIterator = new RecordReaderDataSetIterator(recordReader, batchSize, numInputs, numOutputs);


        // - Normalization >>>
        DataNormalization normalization = new NormalizerMinMaxScaler(0, 1);
        normalization.fit(dataSetIterator);
        dataSetIterator.setPreProcessor(normalization);
        // <<< Normalization -


        // - Configuration >>>
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(seed)
//                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .biasInit(1)
                .l2(1e-4)
                .updater(new Nesterovs(learningRate, 0.9))
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(numInputs)
                        .nOut(numHiddenNodes)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new DenseLayer.Builder()
                        .nIn(numHiddenNodes)
                        .nOut(numHiddenNodes)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.SOFTMAX)
                        .nIn(numHiddenNodes)
                        .nOut(numOutputs)
                        .build())
                .pretrain(false)
                .backprop(true)
                .build();
        // <<< Configuration -


        // - TRAIN >>>
        MultiLayerNetwork model = new MultiLayerNetwork(configuration);
        model.init();

        // - - Server >>>
//        model.setListeners(new ScoreIterationListener(10000));
        UIServer uiServer = UIServer.getInstance();
        StatsStorage statsStorage = new InMemoryStatsStorage();
        uiServer.attach(statsStorage);
        model.setListeners(new StatsListener(statsStorage));
        // <<< Server - -

        for (int x = 0; x < nEpochs; x++) {
            while (dataSetIterator.hasNext())
                model.fit(dataSetIterator.next());
            dataSetIterator.reset();
        }
        // <<< TRAIN -


        // - Save >>>
        ModelSerializer.writeModel(model, "C:\\Users\\hahah\\Desktop\\ML_IN_OUT\\FULL_SIZE\\model.zip", true);
        // <<< Save -


        // - Evaluation >>>
        DataSetIterator dataSetIteratorTest = new RecordReaderDataSetIterator(recordReader, batchSize, numInputs, numOutputs);
        normalization.fit(dataSetIteratorTest);
        dataSetIteratorTest.setPreProcessor(normalization);

        Evaluation evaluation = new Evaluation(numOutputs);
        while (dataSetIteratorTest.hasNext()) {
            DataSet dataSet = dataSetIteratorTest.next();
            INDArray output = model.output(dataSet.getFeatureMatrix());
            evaluation.eval(dataSet.getLabels(), output);
        }

        System.out.println(evaluation.stats());
        // <<< Evaluation -
    }
}
