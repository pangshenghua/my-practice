package com.chengma.core.common;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常信息处理器
 *
 * @author psh 2023/4/18 18:03
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

	/**
	 * 处理Get请求中 使用@Valid 验证路径中请求实体校验失败后抛出的异常
	 */
	@ExceptionHandler(BindException.class)
	@ResponseBody
	public R<String> bindExceptionHandler(BindException e) {
		String message = e.getBindingResult().getAllErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.collect(Collectors.joining());
		return R.ok(message);
	}

	/**
	 * 处理请求参数格式错误 @RequestParam上validate失败后抛出的异常是javax.validation.ConstraintViolationException
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseBody
	public R<String> constraintViolationExceptionHandler(ConstraintViolationException e) {
		String message = e.getConstraintViolations().stream()
				.map(ConstraintViolation::getMessage).collect(Collectors.joining());
		return R.ok(message);
	}

	/**
	 * 处理请求参数格式错误 @RequestBody上validate失败后抛出的异常是MethodArgumentNotValidException异常
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public R<String> methodArgumentNotValidExceptionHandler(
			MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getAllErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.collect(Collectors.joining());
		return R.ok(message);
	}

}
