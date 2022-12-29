
<template>
        <div id="chart" style="width:100%;height:100%;"></div>
</template>
<script>
import echarts from 'echarts'
import { BackgroundStyle } from 'quill'
export default {
    name: 'chart',
    data() {
        return {


            chart: null,

            option: {

                color: ['rgba(180, 180, 180, 0.2)', '#40adfd'],
                title: {
                    text: '报警统计',
                    textStyle: {
                        fontSize: 22,
                        fontWeight: 600,
                        color: '#333',
                        fontFamily: 'Microsoft YaHei'
                    },
                    left: 15,
                    // top: 15
                },
                legend: {},
                grid:{
                    width:500,
                    height:220,
                    left: '5%'
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        // Use axis to trigger tooltip
                        type: 'shadow' // 'shadow' as default; can also be 'line' or 'shadow'
                    },
                    formatter: function (params) {
                        let value1,value2='';
                        params.forEach(function (item) {
                            if (item.seriesIndex == 0) {
                                value1 = item.axisValue + '<br>' + item.marker +  '报警 : ' + item.data
                            } else if (item.seriesIndex == 1) {
                                value2 = item.marker   + '未报警 : ' + item.data
                            } 
                        })
                        return value1 + '<br>' + value2;
                    }
                },
                legend: {},
                xAxis: {
                    type: 'category',
                    data: ['塔顶', '拉杆', '回转平台', '底座', '撑杆', '附着装置'],
                    axisLabel: {
                        interval: 0,
                        align: 'center',
                        margin :16
                        // formatter: function (params) {
                        //     var result = "";
                        //     var x = 2;
                        //     var rowNumber = Math.ceil(params.length / x);


                        //     for (var p = 0; p < params.length; p = p + x) {
                        //         if (Math.ceil(p / x) == rowNumber - 1) {

                        //             result = result + params.substring(p, params.length) + '\n';
                        //             break;
                        //         } else {


                        //             result = result + params.substring(p, p + x) + '\n';
                        //         }
                        //     }
                        //     return result;
                        // }
                    }
                },
                yAxis: {
                    type: 'value',
                    min: 0,
                    max: 40
                },
                series: [
                    {
                        data: [35, 32, 27, 2, 32, 27],
                        type: 'bar',
                        stack: 'xxx',
                        barWidth: 35,

                        // showBackground: true,
                        // backgroundStyle: {
                        //     color: '#40adfd',
                        //     borderColor :'#40adfd'
                        // }
                    },
                    {
                        data: [5, 8, 13, 38, 8, 13],
                        type: 'bar',
                        stack: 'xxx',
                        barWidth: 25,
                        // showBackground: true,
                        // backgroundStyle: {
                        //     color: '#40adfd',
                        //     borderColor :'#40adfd'
                        // }
                    },
                ]
            }
            
        }
    },
    mounted() {
        this.initChart()
    },
    methods: {
        initChart() {
            if (this.chart == null) {

                this.chart = echarts.init(document.getElementById('chart'))
            }

            this.chart.setOption(this.option);

            window.onresize = this.chart.resize()
        }
    }
}
</script>
<style lang="scss" scoped>
// .container{
//     display: flex;
//     flex-direction: row;
//     justify-content: center;
//     align-items: center;
// }
</style>