package com.ruoyi.prediction;

import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
@Slf4j
public class Prediction {
    /**
     * 预测下一周每天的数据。
     *
     * @param data 之前的数据
     * @return 下一周每天的数据。
     * @throws Exception 如果模型训练或预测过程中出现错误。
     */
    public static List<Integer> predictNextWeek(List<Integer> data) throws Exception {
        List<Integer> processedData = preProcess(data);

        // 创建属性列表
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("previousData"));
        attributes.add(new Attribute("currentData"));

        // 创建Instances对象，准备数据
        Instances dataset = new Instances("DailyData", attributes, processedData.size());
        dataset.setClassIndex(dataset.numAttributes() - 1);

        // 将数据添加到Instances对象，跳过第一项，因为它没有前一天的数据
        for (int i = 1; i < processedData.size(); i++) {
            double[] instanceValue = new double[]{
                    processedData.get(i - 1),
                    processedData.get(i)
            };
            dataset.add(new DenseInstance(1.0, instanceValue));
        }

        // 使用线性回归模型
        LinearRegression model = new LinearRegression();
        model.buildClassifier(dataset); // 训练模型

        // 进行模型评估
        Evaluation evaluation = new Evaluation(dataset);

        // 设置交叉验证的折数，确保不超过实例数量 - 1
        int numFolds = Math.min(10, processedData.size() - 1);
        evaluation.crossValidateModel(model, dataset, numFolds, new Random(1));

        // 输出评估结果
        log.info("=== 线性回归模型评估结果 ===");
        log.info(evaluation.toSummaryString());
        log.info("均方根误差 (RMSE): {}", evaluation.rootMeanSquaredError());
        log.info("平均绝对误差 (MAE): {}", evaluation.meanAbsoluteError());
        log.info("相对绝对误差 (%RAE): {}%", evaluation.relativeAbsoluteError());
        log.info("相对平方误差 (%RRSE): {}%", evaluation.rootRelativeSquaredError());
        log.info("线性回归方程: {}", model);

        // 进行预测
        List<Integer> predictions = new ArrayList<>();
        double predictionForNextDay = processedData.get(processedData.size() - 1);
        for (int i = 0; i < 7; i++) {
            DenseInstance nextDayInstance = new DenseInstance(2);
            nextDayInstance.setValue(attributes.get(0), predictionForNextDay);
            nextDayInstance.setDataset(dataset);
            predictionForNextDay = model.classifyInstance(nextDayInstance);
            predictions.add((int) Math.round(predictionForNextDay));
        }

        return predictions;
    }

    private static List<Integer> preProcess(List<Integer> data) {
        if (data.isEmpty()) {
            data.add(0);
            data.add(0);
            data.add(0);
        } else if (data.size() == 1) {
            data.add(data.get(0));
            data.add(data.get(0));
        } else if (data.size() == 2) {
            data.add(data.get(1));
        }
        return data;
    }


    public static void main(String[] args) throws Exception {
        // 生成100天的累计会员数数据
        List<Integer> past100DaysMembers = generate100DaysData();

        // 使用前90天数据进行训练，后10天数据进行评估
        List<Integer> trainingData = past100DaysMembers.subList(0, 90);
        List<Integer> testData = past100DaysMembers.subList(90, 100);

        // 训练并评估模型
        System.out.println("=== 模型训练与评估 ===");
        List<Integer> predictions = predictNextWeek(trainingData);

        // 输出预测结果
        System.out.println("线性回归预测下一周每天的累计会员数: " + predictions);
    }

    /**
     * 生成100天的借阅数据，包含趋势性增长和随机波动。
     *
     * @return 生成的100天借阅数据列表。
     */
    public static List<Integer> generate100DaysData() {
        List<Integer> data = new ArrayList<>();
        Random rand = new Random();
        int base = 50; // 初始借阅量

        for (int i = 1; i <= 100; i++) {
            // 假设每天增加3个单位，加上一些随机波动（-2到+2）
            int value = 50 + i * 3 + rand.nextInt(5) - 2;
            data.add(value);
        }

        return data;
    }
}
