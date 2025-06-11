package com.iyuba.American.constant;

/**
 * 广告测试的key数据
 *
 * 自用数据
 */
public interface AdTestKeyData {
    /**
     * 广告位key配置如下
     *
     * 穿山甲
     * com.iyuba.American 开屏 0073
     * com.iyuba.American Banner 0074
     * com.iyuba.American 插屏 0861
     * com.iyuba.American 模版 0862
     * com.iyuba.American DrawVideo 0863
     * com.iyuba.American 激励视频 0864
     *
     * 优量汇
     * com.iyuba.American 开屏 0865
     * com.iyuba.American Banner 0866
     * com.iyuba.American 插屏 0867
     * com.iyuba.American 模版 0868
     * com.iyuba.American DrawVideo 0869
     * com.iyuba.American 激励视频 0870
     *
     * 百度
     * com.iyuba.American 开屏 0871
     * com.iyuba.American 插屏 0872
     * com.iyuba.American 模版 0873
     * com.iyuba.American 激励视频 0874
     */

    //key值信息
    interface  KeyData{
        //开屏广告
        class SpreadAdKey{
            /**
             * 穿山甲 0073
             * 优量汇 0865
             * 百度 0871
             * 快手
             */
            public static final String spread_youdao = "a710131df1638d888ff85698f0203b46";//有道
            public static final String spread_beizi = "";//倍孜
            public static final String spread_csj = "0073";//穿山甲
            public static final String spread_ylh = "0865";//优量汇
            public static final String spread_baidu = "0871";//百度
            public static final String spread_ks = "";//快手
        }

        //信息流广告
        class TemplateAdKey{
            /**
             * 穿山甲 0862
             * 优量汇 0868
             * 百度 0873
             * 快手
             */
            public static final String template_youdao = "3438bae206978fec8995b280c49dae1e";//有道
            public static final String template_csj = "0862";//穿山甲
            public static final String template_ylh = "0868";//优量汇
            public static final String template_baidu = "0873";//百度
            public static final String template_ks = "";//快手
            public static final String template_vlion = "";//瑞狮
        }

        //banner广告
        class BannerAdKey{
            /**
             * 穿山甲 0074
             * 优量汇 0866
             */
            public static final String banner_youdao = "230d59b7c0a808d01b7041c2d127da95";//有道
            public static final String banner_csj = "0074";//穿山甲
            public static final String banner_ylh = "0866";//优量汇
        }

        //插屏广告
        class InterstitialAdKey{
            /**
             * 穿山甲 0861
             * 优量汇 0867
             * 百度 0872
             * 快手
             */
            public static final String interstitial_csj = "0861";//穿山甲
            public static final String interstitial_ylh = "0867";//优量汇
            public static final String interstitial_baidu = "0872";//百度
            public static final String interstitial_ks = "";//快手
        }

        class DrawVideoAdKey{
            /**
             * 穿山甲 0863
             * 优量汇 0869
             * 快手
             */
            public static final String drawVideo_csj = "0863";//穿山甲
            public static final String drawVideo_ylh = "0869";//优量汇
            public static final String drawVideo_ks = "";//快手
        }

        //激励视频广告
        class IncentiveAdKey{
            /**
             * 穿山甲 0864
             * 优量汇 0870
             * 百度 0874
             * 快手
             */
            public static final String incentive_csj = "0864";//穿山甲
            public static final String incentive_ylh = "0870";//优量汇
            public static final String incentive_baidu = "0874";//百度
            public static final String incentive_ks = "";//快手
        }
    }
}
