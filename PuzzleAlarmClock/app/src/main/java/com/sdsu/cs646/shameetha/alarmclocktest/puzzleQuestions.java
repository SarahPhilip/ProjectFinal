package com.sdsu.cs646.shameetha.alarmclocktest;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Shameetha on 5/7/15.
 */
public class puzzleQuestions {
        enum Operator {
            ADD, SUBTRACT, MULTIPLY, DIVIDE;

            @Override
            public String toString() {
                String string = null;
                switch (ordinal()) {
                    case 0:
                        string = "+";
                        break;
                    case 1:
                        string = "-";
                        break;
                    case 2:
                        string = "*";
                        break;
                    case 3:
                        string = "/";
                        break;
                }
                return string;
            }
        }

        private ArrayList<Float> mParts;
        private ArrayList<Operator> mOperators;
        private float mResult = 0f;
        private int min = 0;
        private int max = 10;
        public puzzleQuestions() {
            this(3);
        }

        public puzzleQuestions(int parts) {
            super();
            Random numberRandom = new Random(System.currentTimeMillis());
            mParts = new ArrayList<>(parts);
            for (int i = 0; i < parts; i++)
                mParts.add(i, (float) numberRandom.nextInt(max - min + 1) + min);

            mOperators = new ArrayList<>(parts - 1);
            for (int i = 0; i < parts - 1; i++)
                mOperators.add(i, Operator.values()[numberRandom.nextInt(2) + 1]);

            ArrayList<Object> allParts = new ArrayList<Object>();
            for (int i = 0; i < parts; i++) {
                allParts.add(mParts.get(i));
                if (i < parts - 1)
                    allParts.add(mOperators.get(i));
            }

            while (allParts.contains(Operator.DIVIDE)) {
                int i = allParts.indexOf(Operator.DIVIDE);
                mResult = (Float) allParts.get(i - 1) / (Float) allParts.get(i + 1);
                for (int r = 0; r < 2; r++)
                    allParts.remove(i - 1);
                allParts.set(i - 1, mResult);
            }
            while (allParts.contains(Operator.MULTIPLY)) {
                int i = allParts.indexOf(Operator.MULTIPLY);
                mResult = (Float) allParts.get(i - 1) * (Float) allParts.get(i + 1);
                for (int r = 0; r < 2; r++)
                    allParts.remove(i - 1);
                allParts.set(i - 1, mResult);
            }

            while (allParts.contains(Operator.ADD)) {
                int i = allParts.indexOf(Operator.ADD);
                mResult = (Float) allParts.get(i - 1) + (Float) allParts.get(i + 1);
                for (int r = 0; r < 2; r++)
                    allParts.remove(i - 1);
                allParts.set(i - 1, mResult);
            }
            while (allParts.contains(Operator.SUBTRACT)) {
                int i = allParts.indexOf(Operator.SUBTRACT);
                mResult = (Float) allParts.get(i - 1) - (Float) allParts.get(i + 1);
                for (int r = 0; r < 2; r++)
                    allParts.remove(i - 1);
                allParts.set(i - 1, mResult);
            }
        }
        @Override
        public String toString() {
            StringBuilder answerBuilder = new StringBuilder();
            for (int i = 0; i < mParts.size(); i++) {
                answerBuilder.append(mParts.get(i));
                answerBuilder.append(" ");
                if (i < mOperators.size()){
                    answerBuilder.append(mOperators.get(i).toString());
                    answerBuilder.append(" ");
                }
            }
            return answerBuilder.toString();
        }
        public float getResult() {
            return mResult;
        }

    }