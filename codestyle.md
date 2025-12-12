# 代码规范（后端）

来源：
- Java：Google Java Style Guide（https://google.github.io/styleguide/javaguide.html）
- Spring：Spring 官方命名与分层最佳实践

核心要求：
- 包命名全小写，类名使用大驼峰，方法与变量使用小驼峰
- 每行不超过 120 列，适当换行
- 控制器、服务、仓库、实体、DTO 分层清晰，避免循环依赖
- 使用 `@Valid` 进行参数校验，异常信息明确
- 禁止无意义的空 `catch`
- 避免过深嵌套，优先使用早返回


