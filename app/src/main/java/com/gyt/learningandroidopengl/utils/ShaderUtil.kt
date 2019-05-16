package com.gyt.learningandroidopengl.utils

import android.opengl.GLES20
import android.util.Log

/**
 * @author gyt
 * @date on 2019-05-14 17:55
 * @describer 着色器工具类
 */
object ShaderUtil {
    private val TAG = "ShaderUtil"

    /**
     * 编译顶点着色器
     */
    fun compileVertexShader(shaderCode: String): Int = compileShader(GLES20.GL_VERTEX_SHADER, shaderCode)

    /**
     * 编译片段着色器
     */
    fun compileFragmentShader(shaderCode: String): Int = compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode)

    /**
     * 编译着色器
     */
    private fun compileShader(type: Int, shaderCode: String): Int {
        // 1.创建一个新的着色器对象
        val shaderObjectId = GLES20.glCreateShader(type)

        // 2.获取创建状态
        if (shaderObjectId == 0) {
            Log.w(TAG, "Could not create new shader")
            return 0
        }

        // 3.将着色器代码上传到着色器对象中
        GLES20.glShaderSource(shaderObjectId, shaderCode)

        // 4.编译着色器对象
        GLES20.glCompileShader(shaderObjectId)

        // 5.获取编译状态：OpenGL将想要获取的值放入长度为1的数组的首位
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

        Log.i(TAG, "Result of compiling source:\n " + shaderCode + "\n:" + GLES20.glGetShaderInfoLog(shaderObjectId))

        // 6.验证编译状态
        if (compileStatus[0] == 0) {
            // 如果编译失败，则删除创建的着色器对象
            GLES20.glDeleteShader(shaderObjectId)
            Log.w(TAG, "Compilation of shader failed")
            // 7.返回着色器对象：失败，为0
            return 0
        }
        // 7.返回着色器对象：成功，非0
        return shaderObjectId
    }

    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        // 1.创建一个OpenGL程序对象
        val programObjectId = GLES20.glCreateProgram()

        // 2.获取创建状态
        if (programObjectId == 0) {
            Log.w(TAG, "Could not create new program")
            return 0
        }

        // 3.将顶点着色器依附到OpenGL程序对象
        GLES20.glAttachShader(programObjectId, vertexShaderId)
        // 3.将片段着色器依附到OpenGL程序对象
        GLES20.glAttachShader(programObjectId, fragmentShaderId)

        // 4.将两个着色器链接到OpenGL程序对象
        GLES20.glLinkProgram(programObjectId)

        // 5.获取链接状态：OpenGL将想要获取的值放入长度为1的数组的首位
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0)

        Log.i(TAG, "Result of linking program:\n " + GLES20.glGetProgramInfoLog(programObjectId))

        // 6.验证链接状态
        if (linkStatus[0] == 0) {
            // 链接失败则删除程序对象
            GLES20.glDeleteProgram(programObjectId)
            Log.w(TAG, "Linking of program failed")
            // 7.返回程序对象：失败，为0
            return 0
        }
        // 7.返回程序对象：成功，非0
        return programObjectId
    }

}