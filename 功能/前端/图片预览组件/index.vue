<template>
  <div class="img-container">
    <div v-for="(url, index) in urls" :key="index">
      <el-image :src="url" :preview-src-list="urls" fit>
        <div slot="error" class="image-slot">
          <i class="el-icon-picture-outline"></i>
        </div>
      </el-image>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      urls: [],
      
    };
  },
  props: {
    urlArr: {
      Array,
    },
  },
  mounted() {
    this.urlArr.forEach((item) => {
      this.urls.push(this.define.APIURl + item.url);
    });
  },
};
</script>
<style scoped>
.img-container {
  width: 100%;
  height: 100%;
  text-align: center;
}

/deep/.el-image__inner {
  width: 40px;
  height: 40px;
}

/deep/.el-image__inner {
  width: 40px;
  height: 40px;
}
</style>