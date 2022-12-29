
<template>

    <el-col style="width:100%;height:100%;">
        <div id="chart1" style="width:100%;height:100%;"></div>
    </el-col>



</template>
<script>
import echarts from 'echarts'
import { BackgroundStyle } from 'quill'
export default {
    name: 'chart',
    data() {
        return {
            //pie
            chart1: null,


            // option: {
            //     title: {
            //         padding: [
            //             20,  // 上
            //             0, // 右
            //             0,  // 下
            //             10, // 左
            //         ],
            //         text: '各线路病害统计',
            //         textStyle: {
            //             fontSize: 22,
            //             fontWeight: 900,
            //             color: '#333'
            //         }
            //     },
            //     grid: {
            //         x: 45,
            //         y: 80,
            //         x2: 20,
            //         y2: 25,
            //         borderWidth: 0
            //     },
            //     xAxis: {

            //         type: 'category',

            //         data: ['1号线', '2号线', '3号线', '4号线', '5号线', '6号线', '7号线', '8号线', '9号线', '10号线','9号线', '10号线','9号线', '10号线'],
            //         axisLabel:{
            //             interval:0
            //         }
            //     },
            //     yAxis: {
            //         type: 'value',
            //         axisLine: {
            //             show: false
            //         },
            //         min: 0,
            //         max: 7000
            //     },
            //     series: [
            //         {   
            //             barWidth:'50%',
            //             // barCategoryGap: "80%",
            //             data: [120, 2000, 150, 80, 70, 110, 130, 70, 110, 130, 110, 130, 110, 130],
            //             type: 'bar',
            //             backgroundStyle: {
            //                 color: '#2BD6E6'
            //             },

            //             itemStyle: {
            //                 normal: {
            //                     label: {
            //                         show: true,
            //                         position: 'top',
            //                         color: '#909399'
            //                     },
            //                     color: '#2BD6E6',
            //                 }
            //             },

            //         },

            //     ]
            // }
            // pie
            option1: {
                color: [
                    '#546fc6',
                    '#40adfd',
                    '#febf70',
                    '#60d96b',
                ],
                title: {
                    text: '在线统计',
                    textStyle: {
                        fontSize: 22,
                        fontWeight: 600,
                        color: '#333',
                        fontFamily: 'Microsoft YaHei'
                    },
                    left: 15,
                    top: 15
                },

                tooltip: {
                    trigger: 'item',
                    formatter: '{b} : {c} ({d}%)'
                },
                legend: {
                    orient: 'vertical',
                    itemWidth: 40,
                    itemHeight: 20,

                    top: 300,
                    left: 50,
                    //间隔
                    itemGap: 20,
                    textStyle: { padding: [0, 40, 0, 15] },//文字右边距50px

                },
                series: [
                    {
                        name: 'Access From',
                        type: 'pie',
                        radius: '50%',
                        center: ['50%', '45%'],
                        label: {
                            show: false
                        },
                        startAngle:280,
                        data: [
                            { value: 190, name: '在线 31座' },
                            { value: 70, name: '部分在线 0座' },
                            { value: 110, name: '离线 0座' },
                            { value: 1, name: '一类基本完好30' },

                        ],
                        emphasis: {
                            label: {
                                show: true
                            },
                            itemStyle: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        },
                        
                    }
                ]
            },


        }
    },
    mounted() {
        this.initChart()
    },
    methods: {
        initChart() {
            if (this.chart1 == null) {
                this.chart1 = echarts.init(document.getElementById('chart1'))
            }
            this.chart1.setOption(this.option1);
            window.onresize = this.chart1.resize()
        }
    }
}
</script>
<style lang="scss" scoped>

</style>